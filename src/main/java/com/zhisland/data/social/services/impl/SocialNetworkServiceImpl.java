/**
 * 
 */
package com.zhisland.data.social.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Tuple;

import com.zhisland.data.social.dtos.UserRelationDto;
import com.zhisland.data.social.mappers.UserRelationMapper;
import com.zhisland.data.social.services.SocialNetworkService;
import com.zhisland.data.social.utils.RedisKeys;

/**
 * @author muzongyan
 *
 */
@Service
public class SocialNetworkServiceImpl implements SocialNetworkService, RedisKeys {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private UserRelationMapper userRelationMapper;

    @Override
    public void addRelation(String userA, String userB) {
        // 判断 userA 和 userB 之前的状态
        // 如果 userA 和 userB 相同则退出
        if (userA == userB) {
            return;
        }

        // redis opration
        try (Jedis jedis = jedisPool.getResource()) {

            // 如果 userA 和 userB 之前是一度关系则退出
            if (jedis.sismember(PREFIX_REL_DEGREE_1 + userA, userB)
                    || jedis.sismember(PREFIX_REL_DEGREE_1 + userB, userA)) {
                return;
            }

            // A原来的好友（除去B原来已经认识的）
            Set<String> userAFriendsNoUserB = jedis.sdiff(PREFIX_REL_DEGREE_1 + userA, PREFIX_REL_DEGREE_1 + userB);

            // B原来的好友（除去A原来已经认识的）
            Set<String> uesrBFriendsNoUserA = jedis.sdiff(PREFIX_REL_DEGREE_1 + userB, PREFIX_REL_DEGREE_1 + userA);

            Pipeline pipeline = jedis.pipelined();
            pipeline.multi();

            // userA 和 userB 无论是否存在二度关系，都做解除操作
            pipeline.zrem(PREFIX_REL_DEGREE_2 + userA, userB);
            pipeline.zrem(PREFIX_REL_DEGREE_2 + userB, userA);

            // 构建一度关系
            pipeline.sadd(PREFIX_REL_DEGREE_1 + userA, userB);
            pipeline.sadd(PREFIX_REL_DEGREE_1 + userB, userA);

            // 构建二度关系
            // A和B成为好友后，A原来的好友（除去B原来已经认识的）就变成B的二度好友，B原来的好友（除去A原来已经认识的）就变成A的二度好友
            // 构建以 userB 为桥梁的二度关系
            // userB 原来的好友（除去 userA 原来已经认识的）通过 userB 成为 userA 的二度好友
            // userA 通过 userB 与 userB 原来的好友（除去 userA 原来已经认识的）成为二度好友
            for (String tmp : uesrBFriendsNoUserA) {
                pipeline.zincrby(PREFIX_REL_DEGREE_2 + userA, 1, tmp);
                pipeline.zincrby(PREFIX_REL_DEGREE_2 + tmp, 1, userA);
            }

            // 构建以 userA 为桥梁的二度关系
            // userA 原来的好友（除去 userB 原来已经认识的）通过 userA 成为 userB 的二度好友
            // userB 通过 userA 与 userA 原来的好友（除去 userB 原来已经认识的）成为二度好友
            for (String tmp : userAFriendsNoUserB) {
                pipeline.zincrby(PREFIX_REL_DEGREE_2 + userB, 1, tmp);
                pipeline.zincrby(PREFIX_REL_DEGREE_2 + tmp, 1, userB);
            }

            pipeline.exec();
            pipeline.sync();
        }
    }

    @Override
    public void removeRelation(String userA, String userB) {
    }

    @Override
    public Set<String> myFirstDegreeRels(String uid) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(PREFIX_REL_DEGREE_1 + uid);
        }
    }

    @Override
    public Map<String, Integer> recommandSecondDegreeRels(String uid, int offset, int count) {
        Set<Tuple> topN;
        try (Jedis jedis = jedisPool.getResource()) {
            topN = jedis.zrevrangeByScoreWithScores(PREFIX_REL_DEGREE_2 + uid, "+inf", "-inf", offset, count);
        }

        Map<String, Integer> rmd = new TreeMap<String, Integer>();
        for (Tuple t : topN) {
            rmd.put(t.getElement(), (int) t.getScore());
        }

        return rmd;
    }

    @Override
    public Set<String> commonFriends(String userA, String userB) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sinter(PREFIX_REL_DEGREE_1 + userA, PREFIX_REL_DEGREE_1 + userB);
        }
    }

    @Override
    public void reloadRelations() {
        // 清空 redis 数据
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }

        int offset = 0;

        while (true) {
            List<UserRelationDto> rels = userRelationMapper.getRels(offset, 10000);

            if (rels == null || rels.isEmpty()) {
                break;
            }

            for (UserRelationDto rel : rels) {
                String userA = String.valueOf(rel.getFromUid());
                String userB = String.valueOf(rel.getToUid());
                this.addRelation(userA, userB);
            }
            offset = offset + 10000;
        }
    }

}
