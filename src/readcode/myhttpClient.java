// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MyHttpClient.java

package com.ebank.commAdapter.update;

import com.alibaba.fastjson.JSON;
import com.ebank.commAdapter.base.Configuration;
import java.io.PrintStream;
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class MyHttpClient
{

    public MyHttpClient()
    {
    }

    public static DefaultHttpClient getClient()
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        return httpClient;
    }

    public static String get(String url, Map params)
    {
        HttpGet httpGet;
        HttpClient httpClient;
        if(params != null)
        {
            StringBuilder sb = new StringBuilder(url);
            if(!url.endsWith("?"))
                sb.append("?");
            Set keySet = params.keySet();
            String key;
            String val;
            for(Iterator i$ = keySet.iterator(); i$.hasNext(); sb.append(key).append("=").append(val).append("&"))
            {
                key = (String)i$.next();
                val = (String)params.get(key);
            }

            url = sb.substring(0, sb.length() - 1);
        }
        httpGet = new HttpGet(url);
        httpClient = getClient();
        HttpResponse httpRes;
        int statusCode;
        httpRes = httpClient.execute(httpGet);
        statusCode = httpRes.getStatusLine().getStatusCode();
        if(statusCode == 200)
            return EntityUtils.toString(httpRes.getEntity());
        break MISSING_BLOCK_LABEL_191;
        Exception e;
        e;
        e.printStackTrace();
        logger.error("", e);
        return null;
    }

    public static String postJson(String url, Map params)
    {
        HttpPost httpPost;
        String string;
        HttpClient httpClient;
        httpPost = new HttpPost(url);
        string = JSON.toJSONString(params);
        httpClient = getClient();
        HttpResponse httpRes;
        HttpEntity entity = new StringEntity(string);
        httpPost.setEntity(entity);
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");
        httpRes = httpClient.execute(httpPost);
        if(200 == httpRes.getStatusLine().getStatusCode())
            return EntityUtils.toString(httpRes.getEntity());
        break MISSING_BLOCK_LABEL_110;
        Exception e;
        e;
        e.printStackTrace();
        logger.error("", e);
        return null;
    }

    public static String post(String url, Map params)
    {
        HttpPost httpPost;
        HttpClient httpClient;
        List paramList;
        httpPost = new HttpPost(url);
        httpClient = getClient();
        paramList = new ArrayList();
        Set sets = params.entrySet();
        String key;
        String val;
        for(Iterator i$ = sets.iterator(); i$.hasNext(); paramList.add(new BasicNameValuePair(key, val)))
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
            key = (String)entry.getKey();
            val = (String)entry.getValue();
        }

        HttpResponse httpRes;
        httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
        httpRes = httpClient.execute(httpPost);
        if(200 == httpRes.getStatusLine().getStatusCode())
            return EntityUtils.toString(httpRes.getEntity());
        break MISSING_BLOCK_LABEL_180;
        Exception e;
        e;
        e.printStackTrace();
        logger.error("", e);
        return null;
    }

    public static void main(String args[])
    {
        String url = "http://localhost:8080/bankFileMgr/deviceMgr/getDeviceCode.html";
        System.out.println((new StringBuilder()).append("url: ").append(url).toString());
        String resStr = get(url, null);
        System.out.println((new StringBuilder()).append("resStr: ").append(resStr).toString());
    }

    private static final Logger logger = Logger.getLogger(com/ebank/commAdapter/update/MyHttpClient);
    public static final String url_getdevicecode;
    public static final String url_adddevice;

    static 
    {
        url_getdevicecode = (new StringBuilder()).append(Configuration.SERVER_ADDRESS).append("/deviceMgr/getDeviceCode.html").toString();
        url_adddevice = (new StringBuilder()).append(Configuration.SERVER_ADDRESS).append("/deviceMgr/addDevice.html").toString();
    }
}
