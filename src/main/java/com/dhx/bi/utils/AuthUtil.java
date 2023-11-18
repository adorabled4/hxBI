package com.dhx.bi.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author adorabled4
 * @className AuthUtil
 * @date : 2023/11/17/ 14:38
 **/
@Slf4j
public class AuthUtil {
    private static final SimpleDateFormat RFC_1123_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    /**
     * 生成认证url
     *
     * @param apiKey    api key
     * @param apiSecret api 秘钥
     * @param host      主机
     * @param path      路径
     * @return {@link String}
     */
    public static String genAuthUrl(String apiKey, String apiSecret, String host, String path)   {
        if (StringUtils.isAnyBlank(apiKey, apiSecret, host, path)) {
            return null;
        }
        try {
            String date = getRFC1123Date();
            String httpUrl = generateAuthorization(date, host, path, apiSecret, apiKey);
            return httpUrl;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取rfc1123日期
     *
     * @return {@link String}
     */
    private static String getRFC1123Date() {
        return RFC_1123_DATE_FORMAT.format(new Date());
    }

    /**
     * 生成授权信息
     *
     * @param host      主机地址
     * @param path      路径
     * @param apiSecret api 秘钥
     * @param apiKey    api key
     * @return {@link String}
     */
    private static String generateAuthorization(String date1, String host, String path, String apiSecret, String apiKey) throws NoSuchAlgorithmException, InvalidKeyException {
        try {
            // 时间
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = format.format(new Date());
            // 拼接
            String preStr = "host: " + host + "\n" +
                    "date: " + date + "\n" +
                    "GET " + path + " HTTP/1.1";
            // SHA256加密
            Mac mac = Mac.getInstance("hmacsha256");
            SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
            mac.init(spec);

            byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
            // Base64加密
            String sha = Base64.getEncoder().encodeToString(hexDigits);
            // 拼接
            String authorizationOrigin = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
            // 拼接地址
            HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + host + path)).newBuilder().
                    addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorizationOrigin.getBytes(StandardCharsets.UTF_8))).
                    addQueryParameter("date", date).
                    addQueryParameter("host", host).
                    build();

            return httpUrl.toString();
        } catch (Exception e) {
            log.error("鉴权方法中发生错误：" + e.getMessage());
            return null;
        }
//        StringBuilder sb = new StringBuilder();
//        sb.append("host: ").append(host).append("\n");
//        sb.append("date: ").append(date).append("\n");
//        sb.append("GET: ").append(path).append("\n").append(" HTTP/1.1");
//        byte[] tmpBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
//        byte[] signatureBytes = hmacSha256(apiSecret.getBytes(StandardCharsets.UTF_8), tmpBytes);
//        String signature = Base64.getEncoder().encodeToString(signatureBytes);
//        String authorizationOrigin = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", signature);
//        // 拼接地址
//        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" +host + path)).newBuilder().
//                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorizationOrigin.getBytes(StandardCharsets.UTF_8))).
//                addQueryParameter("date", date).
//                addQueryParameter("host",host).
//                build();
//
//        return httpUrl.toString();
    }

    /**
     * hmac sha256 加密
     *
     * @param keyBytes
     * @param dataBytes
     * @return {@link byte[]}
     */
    private static byte[] hmacSha256(byte[] keyBytes, byte[] dataBytes) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "hmacsha256");
        mac.init(secretKeySpec);
        return mac.doFinal(dataBytes);
    }

    /**
     * 生成url
     *
     * @param date          日期
     * @param host          主机
     * @param path          路径
     * @param authorization 授权信息
     * @return {@link String}
     * @throws UnsupportedEncodingException 不支持编码异常
     */
    private static String generateUrl(String date, String host, String path, String authorization) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("authorization=").append(URLEncoder.encode(authorization, String.valueOf(StandardCharsets.UTF_8)));
        sb.append("&date=").append(URLEncoder.encode(date, String.valueOf(StandardCharsets.UTF_8)));
        sb.append("&host=").append(URLEncoder.encode(host, String.valueOf(StandardCharsets.UTF_8)));
        return "wss://" + host + path + "?" + sb.toString();
    }
}
