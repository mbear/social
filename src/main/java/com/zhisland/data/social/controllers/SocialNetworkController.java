/**
 * 
 */
package com.zhisland.data.social.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhisland.data.social.jsend.JsonData;
import com.zhisland.data.social.jsend.JsonStruct;
import com.zhisland.data.social.services.SocialNetworkService;

/**
 * @author muzongyan
 *
 */
@Controller
@RequestMapping("/relation")
public class SocialNetworkController {

    @Autowired
    private SocialNetworkService socialService;

    /**
     * 添加关系
     * 
     * @param usera
     * @param userb
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(String usera, String userb) throws JsonGenerationException, JsonMappingException, IOException {
        JsonStruct struct = new JsonStruct();
        JsonData data = new JsonData();

        try {
            socialService.addRelation(usera, userb);
            data.put("result", "ok");
            struct.setData(data);
            struct.setStatusToSuccess();
        } catch (Exception e) {
            struct.setMessage("system exception");
            struct.setStatusToError();

            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, struct);

        return out.toString("UTF-8");
    }

    /**
     * 获得 uid 的一度关系
     * 
     * @param uid
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "1degree", method = RequestMethod.GET)
    public String myFirstDegreeRels(String uid) throws JsonGenerationException, JsonMappingException, IOException {
        JsonStruct struct = new JsonStruct();
        JsonData data = new JsonData();

        try {
            Set<String> rels = socialService.myFirstDegreeRels(uid);
            data.put("result", rels);
            struct.setData(data);
            struct.setStatusToSuccess();
        } catch (Exception e) {
            struct.setMessage("system exception");
            struct.setStatusToError();

            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, struct);

        return out.toString("UTF-8");
    }

    /**
     * 推荐 uid 的二度关系
     * 
     * @param uid
     * @param offset
     * @param count
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "2degree", method = RequestMethod.GET)
    public String recommandSecondDegreeRels(String uid, int offset, int count) throws JsonGenerationException,
            JsonMappingException, IOException {
        JsonStruct struct = new JsonStruct();
        JsonData data = new JsonData();

        try {
            Map<String, Integer> rels = socialService.recommandSecondDegreeRels(uid, offset, count);
            data.put("result", rels);
            struct.setData(data);
            struct.setStatusToSuccess();
        } catch (Exception e) {
            struct.setMessage("system exception");
            struct.setStatusToError();

            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, struct);

        return out.toString("UTF-8");
    }

    /**
     * 获得 userA 和 userB 的共同好友
     * 
     * @param userA
     * @param userB
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "common", method = RequestMethod.GET)
    public String commonFriends(final String usera, final String userb) throws JsonGenerationException,
            JsonMappingException, IOException {
        JsonStruct struct = new JsonStruct();
        JsonData data = new JsonData();

        try {
            Set<String> rels = socialService.commonFriends(usera, userb);
            data.put("result", rels);
            struct.setData(data);
            struct.setStatusToSuccess();
        } catch (Exception e) {
            struct.setMessage("system exception");
            struct.setStatusToError();

            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, struct);

        return out.toString("UTF-8");
    }
}
