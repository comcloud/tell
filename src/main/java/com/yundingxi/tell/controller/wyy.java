package com.yundingxi.tell.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yundingxi.tell.bean.entity.Comments;
import com.yundingxi.tell.mapper.CommentsMapper;
import com.yundingxi.tell.mapper.SpittingGroovesMapper;
import com.yundingxi.tell.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Auyher: Ktry
 * @Date: 2020/3/22 22:25
 */
@RestController
@RequestMapping
@Controller
public class wyy {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SpittingGroovesMapper spittingGroovesMapper;
    @Autowired
    private CommentsMapper c;
    @ResponseBody
    @GetMapping("/buzz111")
    public BaseResult buzz(@RequestParam(required = false,defaultValue = "") ArrayList<String> id)  throws Exception {
        for (String s : id) {
            System.out.println(s);
        }
        for (String s : id) {
            System.out.println(s);
            CloseableHttpClient closeableHttpClient = HttpClients.createDefault() ;
            HttpPost httpPost = new HttpPost("http://music.163.com/weapi/v1/resource/comments/R_SO_4_"+s+"?csrf_token=") ;
            httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("params","RlBC7U1bfy/boPwg9ag7/a7AjkQOgsIfd+vsUjoMY2tyQCPFgnNoxHeCY+ZuHYqtM1zF8DWIBwJWbsCOQ6ZYxBiPE3bk+CI1U6Htoc4P9REBePlaiuzU4M3rDAxtMfNN3y0eimeq3LVo28UoarXs2VMWkCqoTXSi5zgKEKbxB7CmlBJAP9pn1aC+e3+VOTr0"));
            list.add(new BasicNameValuePair("encSecKey","76a0d8ff9f6914d4f59be6b3e1f5d1fc3998317195464f00ee704149bc6672c587cd4a37471e3a777cb283a971d6b9205ce4a7187e682bdaefc0f225fb9ed1319f612243096823ddec88b6d6ea18f3fec883d2489d5a1d81cb5dbd0602981e7b49db5543b3d9edb48950e113f3627db3ac61cbc71d811889d68ff95d0eba04e9"));

            httpPost.setEntity(new UrlEncodedFormEntity(list));
            CloseableHttpResponse response=closeableHttpClient.execute(httpPost);

            HttpEntity entity=response.getEntity();
            String ux = EntityUtils.toString(entity,"utf-8") ;
            JSONObject jsonObject = JSON.parseObject(ux);

            LinkedHashMap<String, ArrayList> map = new LinkedHashMap<>();
            map.put("hot",Text1(jsonObject.getJSONArray("hotComments")));
            map.put("newest",Text1(jsonObject.getJSONArray("comments")));
        }
        return new BaseResult("200","ok");
    }

    public ArrayList Text1(JSONArray objects) throws UnsupportedEncodingException {
        ArrayList List = new ArrayList();
        for (Object v : objects) {
            JSONObject object = (JSONObject) v;
            System.out.println(object.getString("content"));
            java.util.List<String> strings = userMapper.selectAllOpenId();
            java.util.List<String> allID = spittingGroovesMapper.getAllID();
            Comments s= new Comments(UUID.randomUUID()+"", strings.get((int)(Math.random()*strings.size())),object.getString("content" ),"0",new Date(),allID.get((int)(Math.random()*allID.size())));
            c.insert(s);
            System.out.println("==================插入成功");
        }
        return List;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public class BaseResult{
        private String code;
        private Object data;
    }

}