/**
 * 
 */
package com.zhisland.data.social.services;

import java.util.Map;
import java.util.Set;

/**
 * 社会化网络服务
 * 
 * @author muzongyan
 *
 */
public interface SocialNetworkService {

    /**
     * 添加关系<br/>
     * userA 和 userB 成为一度关系
     * 
     * @param userA
     * @param userB
     */
    public void addRelation(final String userA, final String userB);

    /**
     * 解除关系<br/>
     * userA 和 userB 解除一度关系
     * 
     * @param userA
     * @param userB
     */
    public void removeRelation(final String userA, final String userB);

    /**
     * 获得 uid 的一度关系
     * 
     * @param uid
     * @return
     */
    public Set<String> myFirstDegreeRels(final String uid);

    /**
     * 推荐 uid 的二度关系<br/>
     * 按照共同好友数量由高到低排序，共同好友数最多的推荐
     * 
     * @param uid
     * @param offset
     * @param count
     * @return
     */
    public Map<String, Integer> recommandSecondDegreeRels(final String uid, final int offset, final int count);

    /**
     * 获得 userA 和 userB 的共同好友
     * 
     * @param userA
     * @param userB
     * @return
     */
    public Set<String> commonFriends(final String userA, final String userB);
    
    /**
     * 从 mysql 的一度关系表重载数据初始化 redis 中的一度二度关系
     */
    public void reloadRelations();

}
