/**
 * 
 */
package com.zhisland.data.social;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.zhisland.data.social.services.SocialNetworkService;
import com.zhisland.data.social.utils.RedisKeys;

/**
 * @author muzongyan
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext.xml")
public class SocialNetworkServiceTest implements RedisKeys {

    @Autowired
    private SocialNetworkService socialNetworkService;

    @Autowired
    private JedisPool jedisPool;

    @Before
    public void setUp() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCase1() {
        socialNetworkService.addRelation("1", "4");
        socialNetworkService.addRelation("1", "0");
        socialNetworkService.addRelation("0", "2");
        socialNetworkService.addRelation("0", "3");
        socialNetworkService.addRelation("3", "5");
        socialNetworkService.addRelation("10", "11");
        socialNetworkService.addRelation("10", "12");
        socialNetworkService.addRelation("10", "13");

        Set<String> actuals = socialNetworkService.myFirstDegreeRels("0");
        Set<String> expecteds = new HashSet<String>();
        expecteds.add("1");
        expecteds.add("2");
        expecteds.add("3");
        Assert.assertEquals(expecteds, actuals);

        try (Jedis jedis = jedisPool.getResource()) {

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "0", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("4");
            expecteds.add("5");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "1", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("2");
            expecteds.add("3");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "11", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("12");
            expecteds.add("13");
            Assert.assertEquals(expecteds, actuals);

            socialNetworkService.addRelation("0", "10");

            actuals = socialNetworkService.myFirstDegreeRels("0");
            expecteds = new HashSet<String>();
            expecteds.add("1");
            expecteds.add("2");
            expecteds.add("3");
            expecteds.add("10");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "0", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("4");
            expecteds.add("5");
            expecteds.add("11");
            expecteds.add("12");
            expecteds.add("13");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "1", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("2");
            expecteds.add("3");
            expecteds.add("10");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "11", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("12");
            expecteds.add("13");
            expecteds.add("0");
            Assert.assertEquals(expecteds, actuals);
        }
    }

    @Test
    public void testCase2() {
        socialNetworkService.addRelation("1", "4");
        socialNetworkService.addRelation("1", "0");
        socialNetworkService.addRelation("2", "0");
        socialNetworkService.addRelation("0", "3");
        socialNetworkService.addRelation("3", "5");
        socialNetworkService.addRelation("0", "10");
        socialNetworkService.addRelation("10", "11");
        socialNetworkService.addRelation("10", "12");
        socialNetworkService.addRelation("10", "13");

        Set<String> actuals = socialNetworkService.myFirstDegreeRels("0");
        Set<String> expecteds = new HashSet<String>();
        expecteds.add("1");
        expecteds.add("2");
        expecteds.add("3");
        expecteds.add("10");
        Assert.assertEquals(expecteds, actuals);

        actuals = socialNetworkService.myFirstDegreeRels("1");
        expecteds = new HashSet<String>();
        expecteds.add("0");
        expecteds.add("4");
        Assert.assertEquals(expecteds, actuals);

        actuals = socialNetworkService.myFirstDegreeRels("2");
        expecteds = new HashSet<String>();
        expecteds.add("0");
        Assert.assertEquals(expecteds, actuals);

        try (Jedis jedis = jedisPool.getResource()) {
            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "0", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("4");
            expecteds.add("5");
            expecteds.add("11");
            expecteds.add("12");
            expecteds.add("13");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "1", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("2");
            expecteds.add("3");
            expecteds.add("10");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "2", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("1");
            expecteds.add("3");
            expecteds.add("10");
            Assert.assertEquals(expecteds, actuals);
        }

        socialNetworkService.addRelation("1", "2");

        actuals = socialNetworkService.myFirstDegreeRels("0");
        expecteds = new HashSet<String>();
        expecteds.add("1");
        expecteds.add("2");
        expecteds.add("3");
        expecteds.add("10");
        Assert.assertEquals(expecteds, actuals);

        actuals = socialNetworkService.myFirstDegreeRels("1");
        expecteds = new HashSet<String>();
        expecteds.add("0");
        expecteds.add("4");
        expecteds.add("2");
        Assert.assertEquals(expecteds, actuals);

        actuals = socialNetworkService.myFirstDegreeRels("2");
        expecteds = new HashSet<String>();
        expecteds.add("0");
        expecteds.add("1");
        Assert.assertEquals(expecteds, actuals);

        try (Jedis jedis = jedisPool.getResource()) {
            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "0", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("4");
            expecteds.add("5");
            expecteds.add("11");
            expecteds.add("12");
            expecteds.add("13");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "1", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("3");
            expecteds.add("10");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "2", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("3");
            expecteds.add("4");
            expecteds.add("10");
            Assert.assertEquals(expecteds, actuals);
        }
    }

    @Test
    public void testCase3() {
        socialNetworkService.addRelation("1", "4");
        socialNetworkService.addRelation("1", "0");
        socialNetworkService.addRelation("2", "0");
        socialNetworkService.addRelation("3", "0");
        socialNetworkService.addRelation("3", "5");
        socialNetworkService.addRelation("0", "6");
        socialNetworkService.addRelation("6", "10");
        socialNetworkService.addRelation("10", "11");
        socialNetworkService.addRelation("10", "12");
        socialNetworkService.addRelation("10", "13");

        Set<String> actuals = socialNetworkService.myFirstDegreeRels("0");
        Set<String> expecteds = new HashSet<String>();
        expecteds.add("1");
        expecteds.add("2");
        expecteds.add("3");
        expecteds.add("6");
        Assert.assertEquals(expecteds, actuals);

        actuals = socialNetworkService.myFirstDegreeRels("12");
        expecteds = new HashSet<String>();
        expecteds.add("10");
        Assert.assertEquals(expecteds, actuals);

        try (Jedis jedis = jedisPool.getResource()) {
            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "0", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("4");
            expecteds.add("5");
            expecteds.add("10");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "12", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("6");
            expecteds.add("11");
            expecteds.add("13");
            Assert.assertEquals(expecteds, actuals);
        }

        socialNetworkService.addRelation("0", "10");

        actuals = socialNetworkService.myFirstDegreeRels("0");
        expecteds = new HashSet<String>();
        expecteds.add("1");
        expecteds.add("2");
        expecteds.add("3");
        expecteds.add("6");
        expecteds.add("10");
        Assert.assertEquals(expecteds, actuals);

        actuals = socialNetworkService.myFirstDegreeRels("12");
        expecteds = new HashSet<String>();
        expecteds.add("10");
        Assert.assertEquals(expecteds, actuals);

        try (Jedis jedis = jedisPool.getResource()) {
            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "0", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("4");
            expecteds.add("5");
            expecteds.add("11");
            expecteds.add("12");
            expecteds.add("13");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "12", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("6");
            expecteds.add("11");
            expecteds.add("13");
            expecteds.add("0");
            Assert.assertEquals(expecteds, actuals);
        }
    }

    @Test
    public void testCase4() {
        socialNetworkService.addRelation("1", "4");
        socialNetworkService.addRelation("1", "0");
        socialNetworkService.addRelation("2", "0");
        socialNetworkService.addRelation("3", "0");
        socialNetworkService.addRelation("3", "5");
        socialNetworkService.addRelation("0", "6");
        socialNetworkService.addRelation("0", "7");
        socialNetworkService.addRelation("6", "10");
        socialNetworkService.addRelation("7", "10");
        socialNetworkService.addRelation("10", "11");
        socialNetworkService.addRelation("10", "12");
        socialNetworkService.addRelation("10", "13");

        Set<String> actuals = socialNetworkService.myFirstDegreeRels("0");
        Set<String> expecteds = new HashSet<String>();
        expecteds.add("1");
        expecteds.add("2");
        expecteds.add("3");
        expecteds.add("6");
        expecteds.add("7");
        Assert.assertEquals(expecteds, actuals);

        actuals = socialNetworkService.myFirstDegreeRels("12");
        expecteds = new HashSet<String>();
        expecteds.add("10");
        Assert.assertEquals(expecteds, actuals);

        try (Jedis jedis = jedisPool.getResource()) {
            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "0", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("4");
            expecteds.add("5");
            expecteds.add("10");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "12", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("6");
            expecteds.add("7");
            expecteds.add("11");
            expecteds.add("13");
            Assert.assertEquals(expecteds, actuals);
        }

        socialNetworkService.addRelation("0", "10");

        actuals = socialNetworkService.myFirstDegreeRels("0");
        expecteds = new HashSet<String>();
        expecteds.add("1");
        expecteds.add("2");
        expecteds.add("3");
        expecteds.add("6");
        expecteds.add("7");
        expecteds.add("10");
        Assert.assertEquals(expecteds, actuals);

        actuals = socialNetworkService.myFirstDegreeRels("12");
        expecteds = new HashSet<String>();
        expecteds.add("10");
        Assert.assertEquals(expecteds, actuals);

        try (Jedis jedis = jedisPool.getResource()) {
            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "0", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("4");
            expecteds.add("5");
            expecteds.add("11");
            expecteds.add("12");
            expecteds.add("13");
            Assert.assertEquals(expecteds, actuals);

            actuals = jedis.zrange(PREFIX_REL_DEGREE_2 + "12", 0, -1);
            expecteds = new HashSet<String>();
            expecteds.add("6");
            expecteds.add("7");
            expecteds.add("11");
            expecteds.add("13");
            expecteds.add("0");
            Assert.assertEquals(expecteds, actuals);
        }
    }

    @Test
    public void testCase5() {
        socialNetworkService.addRelation("1", "4");
        socialNetworkService.addRelation("1", "0");
        socialNetworkService.addRelation("2", "0");
        socialNetworkService.addRelation("3", "0");
        socialNetworkService.addRelation("3", "5");
        socialNetworkService.addRelation("5", "9");
        socialNetworkService.addRelation("0", "9");
        socialNetworkService.addRelation("0", "6");
        socialNetworkService.addRelation("0", "7");
        socialNetworkService.addRelation("0", "8");
        socialNetworkService.addRelation("10", "6");
        socialNetworkService.addRelation("10", "7");
        socialNetworkService.addRelation("10", "8");
        socialNetworkService.addRelation("10", "11");
        socialNetworkService.addRelation("10", "12");
        socialNetworkService.addRelation("10", "13");

        Map<String, Integer> actuals = socialNetworkService.recommandSecondDegreeRels("0", 0, 3);
        Map<String, Integer> expecteds = new TreeMap<String, Integer>();
        expecteds.put("10", 3);
        expecteds.put("5", 2);
        expecteds.put("4", 1);
        Assert.assertEquals(expecteds, actuals);
    }

}
