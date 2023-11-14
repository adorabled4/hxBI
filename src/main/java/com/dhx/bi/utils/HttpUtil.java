package com.dhx.bi.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpMethod;

import java.io.IOException;

/**
 * @author adorabled4
 * @className HttpUtil
 * @date : 2023/11/14/ 10:49
 **/
public class HttpUtil {

    public static JSONObject execute(String url, HttpMethod method) {
        HttpRequestBase http = null;
        try {
            HttpClient client = HttpClients.createDefault();
            if (method.equals(HttpMethod.GET)) {
                http = new HttpGet(url);
            } else {
                http = new HttpPost(url);
            }
            HttpEntity entity = client.execute(http).getEntity();
            return JSONUtil.parseObj(EntityUtils.toString(entity));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            http.releaseConnection();
        }
    }
}
