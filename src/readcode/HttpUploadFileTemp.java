// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HttpUploadFileTemp.java

package com.ebank.commAdapter.http;

import com.alibaba.fastjson.*;
import com.alibaba.fastjson.parser.Feature;
import com.ebank.commAdapter.base.CommAdapterException;
import com.ebank.commAdapter.base.Configuration;
import com.ebank.commAdapter.update.MyHttpClient;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUploadFileTemp
{

    public HttpUploadFileTemp()
    {
    }

    public static String postFile(byte content[], byte signData[], byte signSvg[], Map paramap, List pathList, byte signImg[], String jsonContent)
        throws CommAdapterException
    {
label0:
        {
            HttpPost post = null;
            Map map;
            String s;
            try
            {
                if(content == null || content.length == 0)
                    throw new CommAdapterException("1024", "\u51ED\u8BC1\u6587\u4EF6\u4E3A\u7A7A");
                DefaultHttpClient httpClient = MyHttpClient.getClient();
                String address = (new StringBuilder()).append(Configuration.SERVER_ADDRESS).append("bankFileInfoMgr/uploadToLocal.html").toString();
                InputStreamBody isb = new InputStreamBody(new ByteArrayInputStream(content), (new StringBuilder()).append(System.currentTimeMillis()).append(".pdf").toString());
                String tellerId = (String)paramap.get("jiaoyigy");
                String transNo = (String)paramap.get("jiaoyils");
                String transId = (String)paramap.get("jiaoyima");
                String transAmount = (String)paramap.get("lf_tranAmt");
                String customerName = (String)paramap.get("lf_customerName");
                String transTime = (String)paramap.get("jiaoyirq");
                String cardNo = (String)paramap.get("lf_cardAccNo");
                MultipartEntity entity = new MultipartEntity();
                int i = 1;
                Iterator i$ = pathList.iterator();
                do
                {
                    if(!i$.hasNext())
                        break;
                    String attachFile = (String)i$.next();
                    File f = new File(attachFile);
                    if(f.exists())
                    {
                        InputStream imgbis = new FileInputStream(attachFile);
                        InputStreamBody imgisb = new InputStreamBody(imgbis, (new StringBuilder()).append("attachFile").append(i).append(".jpg").toString());
                        long len = imgisb.getInputStream().available();
                        if(len == 0L)
                            throw new CommAdapterException("1024", (new StringBuilder()).append(attachFile).append("\u6587\u4EF6\u5927\u5C0F\u4E3A0kb\uFF0C\u4E0D\u80FD\u4E0A\u4F20").toString());
                        entity.addPart((new StringBuilder()).append("attachFile").append(i).toString(), imgisb);
                        i++;
                    }
                } while(true);
                entity.addPart("certificateFile", isb);
                if(signData != null)
                    entity.addPart("signData", new InputStreamBody(new ByteArrayInputStream(signData), (new StringBuilder()).append(System.currentTimeMillis()).append(".data").toString()));
                if(signSvg != null)
                    entity.addPart("signSvg", new InputStreamBody(new ByteArrayInputStream(signSvg), (new StringBuilder()).append(System.currentTimeMillis()).append(".svg").toString()));
                Charset charSet = Charset.forName("UTF-8");
                entity.addPart("cardNo", new StringBody(cover(cardNo), charSet));
                entity.addPart("tellerId", new StringBody(cover(tellerId), charSet));
                entity.addPart("transNo", new StringBody(cover(transNo), charSet));
                entity.addPart("transAmount", new StringBody(cover(transAmount), charSet));
                entity.addPart("customerName", new StringBody(cover(customerName), charSet));
                entity.addPart("transTime", new StringBody(cover(transTime), charSet));
                entity.addPart("transId", new StringBody(cover(transId), charSet));
                entity.addPart("tellerTranData", new StringBody(cover(jsonContent), charSet));
                post = new HttpPost(address);
                post.setEntity(entity);
                HttpResponse response = httpClient.execute(post);
                logger.debug((new StringBuilder()).append("\u54CD\u5E94\u72B6\u6001:").append(response.getStatusLine().getStatusCode()).toString());
                if(response.getStatusLine().getStatusCode() != 200)
                    break MISSING_BLOCK_LABEL_1019;
                HttpEntity he = response.getEntity();
                long lentgth = he.getContentLength();
                if(lentgth <= 0L)
                    lentgth = Long.parseLong(response.getFirstHeader("Content-Length").getValue());
                InputStream in = he.getContent();
                int off = 0;
                int tlen = (int)lentgth;
                byte contentBuf[] = new byte[tlen];
                do
                {
                    if(off >= tlen)
                        break;
                    int len = in.read(contentBuf, off, tlen - off);
                    if(len <= 0)
                        break;
                    off += len;
                } while(true);
                String resMsg = new String(contentBuf, "UTF-8");
                logger.debug((new StringBuilder()).append("\u8FD4\u56DE\u4FE1\u606F==").append(resMsg).toString());
                map = (Map)JSON.parseObject(resMsg, new TypeReference() {

                }, new Feature[0]);
                if(!"succ".equals(map.get("result")))
                    break label0;
                logger.debug((new StringBuilder()).append("return image path==").append((String)map.get("path")).toString());
                s = (String)map.get("path");
            }
            catch(CommAdapterException e)
            {
                throw e;
            }
            catch(Exception e)
            {
                logger.error("", e);
                throw new CommAdapterException("1005");
            }
            finally
            {
                if(post != null)
                    post.abort();
                throw exception;
            }
            if(post != null)
                post.abort();
            return s;
        }
        logger.error("upload file to server failed");
        throw new CommAdapterException("1005", (String)map.get("message"));
        throw new CommAdapterException("1005");
    }

    public static String uploadFile(byte content[], byte signData[], byte signSvg[], byte fingerImg[], List pathList, String jsonContent)
        throws CommAdapterException
    {
label0:
        {
            HttpPost post = null;
            Map map;
            String s;
            try
            {
                if(content == null || content.length == 0)
                    throw new CommAdapterException("1024", "\u51ED\u8BC1\u6587\u4EF6\u4E3A\u7A7A");
                DefaultHttpClient httpClient = MyHttpClient.getClient();
                String address = (new StringBuilder()).append(Configuration.SERVER_ADDRESS).append("bankFileInfoMgr/uploadToLocal2.html").toString();
                InputStreamBody isb = new InputStreamBody(new ByteArrayInputStream(content), (new StringBuilder()).append(System.currentTimeMillis()).append(".pdf").toString());
                JSONObject paramap = JSON.parseObject(jsonContent);
                String tellerId = (String)paramap.get("jiaoyigy");
                String jiaoyijg = (String)paramap.get("jiaoyijg");
                String sealPosX = (String)paramap.get("sealPosX");
                String sealPosY = (String)paramap.get("sealPosY");
                String transDetail = paramap.getJSONArray("transDetail").toJSONString();
                MultipartEntity entity = new MultipartEntity();
                int i = 1;
                if(pathList != null && pathList.size() > 0)
                {
                    Iterator i$ = pathList.iterator();
                    do
                    {
                        if(!i$.hasNext())
                            break;
                        String attachFile = (String)i$.next();
                        File f = new File(attachFile);
                        if(f.exists())
                        {
                            InputStream imgbis = new FileInputStream(attachFile);
                            InputStreamBody imgisb = new InputStreamBody(imgbis, (new StringBuilder()).append("attachFile").append(i).append(".jpg").toString());
                            long len = imgisb.getInputStream().available();
                            if(len == 0L)
                                throw new CommAdapterException("1024", (new StringBuilder()).append(attachFile).append("\u6587\u4EF6\u5927\u5C0F\u4E3A0kb\uFF0C\u4E0D\u80FD\u4E0A\u4F20").toString());
                            entity.addPart((new StringBuilder()).append("attachFile").append(i).toString(), imgisb);
                            i++;
                        }
                    } while(true);
                }
                entity.addPart("certificateFile", isb);
                if(signData != null)
                    entity.addPart("signData", new InputStreamBody(new ByteArrayInputStream(signData), (new StringBuilder()).append(System.currentTimeMillis()).append(".data").toString()));
                if(signSvg != null)
                    entity.addPart("signSvg", new InputStreamBody(new ByteArrayInputStream(signSvg), (new StringBuilder()).append(System.currentTimeMillis()).append(".svg").toString()));
                if(fingerImg != null)
                    entity.addPart("fingerprintImageFile", new InputStreamBody(new ByteArrayInputStream(fingerImg), (new StringBuilder()).append(System.currentTimeMillis()).append(".bmp").toString()));
                Charset charSet = Charset.forName("UTF-8");
                entity.addPart("tellerId", new StringBody(cover(tellerId), charSet));
                entity.addPart("tellerTranData", new StringBody(cover(jsonContent), charSet));
                entity.addPart("jiaoyijg", new StringBody(cover(jiaoyijg), charSet));
                entity.addPart("sealPosX", new StringBody(cover(sealPosX), charSet));
                entity.addPart("sealPosY", new StringBody(cover(sealPosY), charSet));
                entity.addPart("transDetail", new StringBody(cover(transDetail), charSet));
                post = new HttpPost(address);
                post.setEntity(entity);
                logger.debug((new StringBuilder()).append("[\u4E0A\u4F20\u51ED\u8BC1\u81F3\u65E0\u7EB8\u5316\u670D\u52A1\u5668,\u4E0A\u4F20\u53C2\u6570\u4E3A:]").append(jsonContent).toString());
                HttpResponse response = httpClient.execute(post);
                logger.debug((new StringBuilder()).append("\u54CD\u5E94\u72B6\u6001:").append(response.getStatusLine().getStatusCode()).toString());
                if(response.getStatusLine().getStatusCode() != 200)
                    break MISSING_BLOCK_LABEL_1043;
                HttpEntity he = response.getEntity();
                long lentgth = he.getContentLength();
                if(lentgth <= 0L)
                    lentgth = Long.parseLong(response.getFirstHeader("Content-Length").getValue());
                InputStream in = he.getContent();
                int off = 0;
                int tlen = (int)lentgth;
                byte contentBuf[] = new byte[tlen];
                do
                {
                    if(off >= tlen)
                        break;
                    int len = in.read(contentBuf, off, tlen - off);
                    if(len <= 0)
                        break;
                    off += len;
                } while(true);
                String resMsg = new String(contentBuf, "UTF-8");
                logger.debug((new StringBuilder()).append("\u8FD4\u56DE\u4FE1\u606F==").append(resMsg).toString());
                map = (Map)JSON.parseObject(resMsg, new TypeReference() {

                }, new Feature[0]);
                if(!"succ".equals(map.get("result")))
                    break label0;
                logger.debug((new StringBuilder()).append("return image path==").append((String)map.get("path")).toString());
                s = (String)map.get("path");
            }
            catch(CommAdapterException e)
            {
                throw e;
            }
            catch(Exception e)
            {
                logger.error("", e);
                throw new CommAdapterException("1005");
            }
            finally
            {
                if(post != null)
                    post.abort();
                throw exception;
            }
            if(post != null)
                post.abort();
            return s;
        }
        logger.error("upload file to server failed");
        throw new CommAdapterException("1005", (String)map.get("message"));
        throw new CommAdapterException("1005");
    }

    public static String uploadFileCart(byte content[], byte signData[], byte signSvg[], byte fingerImg[], List pathList, String jsonContent)
        throws CommAdapterException
    {
label0:
        {
            HttpPost post = null;
            Map map;
            String s;
            try
            {
                if(content == null || content.length == 0)
                    throw new CommAdapterException("1024", "\u51ED\u8BC1\u6587\u4EF6\u4E3A\u7A7A");
                DefaultHttpClient httpClient = MyHttpClient.getClient();
                String address = (new StringBuilder()).append(Configuration.SERVER_ADDRESS).append("bankFileInfoMgr/uploadToLocal3.html").toString();
                InputStreamBody isb = new InputStreamBody(new ByteArrayInputStream(content), (new StringBuilder()).append(System.currentTimeMillis()).append(".pdf").toString());
                JSONObject paramap = JSON.parseObject(jsonContent);
                String tellerId = (String)paramap.get("jiaoyigy");
                String jiaoyijg = (String)paramap.get("jiaoyijg");
                String sealPosX = (String)paramap.get("sealPosX");
                String sealPosY = (String)paramap.get("sealPosY");
                String batchNo = (String)paramap.get("batchNo");
                String transDetail = paramap.getJSONArray("transDetail").toJSONString();
                MultipartEntity entity = new MultipartEntity();
                int i = 1;
                if(pathList != null && pathList.size() > 0)
                {
                    Iterator i$ = pathList.iterator();
                    do
                    {
                        if(!i$.hasNext())
                            break;
                        String attachFile = (String)i$.next();
                        File f = new File(attachFile);
                        if(f.exists())
                        {
                            InputStream imgbis = new FileInputStream(attachFile);
                            InputStreamBody imgisb = new InputStreamBody(imgbis, (new StringBuilder()).append("attachFile").append(i).append(".jpg").toString());
                            long len = imgisb.getInputStream().available();
                            if(len == 0L)
                                throw new CommAdapterException("1024", (new StringBuilder()).append(attachFile).append("\u6587\u4EF6\u5927\u5C0F\u4E3A0kb\uFF0C\u4E0D\u80FD\u4E0A\u4F20").toString());
                            entity.addPart((new StringBuilder()).append("attachFile").append(i).toString(), imgisb);
                            i++;
                        }
                    } while(true);
                }
                entity.addPart("certificateFile", isb);
                if(signData != null)
                    entity.addPart("signData", new InputStreamBody(new ByteArrayInputStream(signData), (new StringBuilder()).append(System.currentTimeMillis()).append(".data").toString()));
                if(signSvg != null)
                    entity.addPart("signSvg", new InputStreamBody(new ByteArrayInputStream(signSvg), (new StringBuilder()).append(System.currentTimeMillis()).append(".svg").toString()));
                if(fingerImg != null)
                    entity.addPart("fingerprintImageFile", new InputStreamBody(new ByteArrayInputStream(fingerImg), (new StringBuilder()).append(System.currentTimeMillis()).append(".bmp").toString()));
                Charset charSet = Charset.forName("UTF-8");
                entity.addPart("tellerId", new StringBody(cover(tellerId), charSet));
                entity.addPart("tellerTranData", new StringBody(cover(jsonContent), charSet));
                entity.addPart("jiaoyijg", new StringBody(cover(jiaoyijg), charSet));
                entity.addPart("sealPosX", new StringBody(cover(sealPosX), charSet));
                entity.addPart("sealPosY", new StringBody(cover(sealPosY), charSet));
                entity.addPart("batchNo", new StringBody(cover(batchNo), charSet));
                entity.addPart("transDetail", new StringBody(cover(transDetail), charSet));
                post = new HttpPost(address);
                post.setEntity(entity);
                logger.debug((new StringBuilder()).append("[\u4E0A\u4F20\u51ED\u8BC1\u81F3\u65E0\u7EB8\u5316\u670D\u52A1\u5668,\u4E0A\u4F20\u53C2\u6570\u4E3A:]").append(jsonContent).toString());
                HttpResponse response = httpClient.execute(post);
                logger.debug((new StringBuilder()).append("\u54CD\u5E94\u72B6\u6001:").append(response.getStatusLine().getStatusCode()).toString());
                if(response.getStatusLine().getStatusCode() != 200)
                    break MISSING_BLOCK_LABEL_1076;
                HttpEntity he = response.getEntity();
                long lentgth = he.getContentLength();
                if(lentgth <= 0L)
                    lentgth = Long.parseLong(response.getFirstHeader("Content-Length").getValue());
                InputStream in = he.getContent();
                int off = 0;
                int tlen = (int)lentgth;
                byte contentBuf[] = new byte[tlen];
                do
                {
                    if(off >= tlen)
                        break;
                    int len = in.read(contentBuf, off, tlen - off);
                    if(len <= 0)
                        break;
                    off += len;
                } while(true);
                String resMsg = new String(contentBuf, "UTF-8");
                logger.debug((new StringBuilder()).append("\u8FD4\u56DE\u4FE1\u606F==").append(resMsg).toString());
                map = (Map)JSON.parseObject(resMsg, new TypeReference() {

                }, new Feature[0]);
                if(!"succ".equals(map.get("result")))
                    break label0;
                logger.debug((new StringBuilder()).append("return image path==").append((String)map.get("path")).toString());
                s = (String)map.get("path");
            }
            catch(CommAdapterException e)
            {
                throw e;
            }
            catch(Exception e)
            {
                logger.error("", e);
                throw new CommAdapterException("1005");
            }
            finally
            {
                if(post != null)
                    post.abort();
                throw exception;
            }
            if(post != null)
                post.abort();
            return s;
        }
        logger.error("upload file to server failed");
        throw new CommAdapterException("1005", (String)map.get("message"));
        throw new CommAdapterException("1005");
    }

    private static String cover(String str)
    {
        if(StringUtils.isBlank(str))
            return "";
        else
            return str;
    }

    private static boolean validate(String param)
    {
        return param == null || "".equals(param);
    }

    private static Logger logger = LoggerFactory.getLogger(com/ebank/commAdapter/http/HttpUploadFileTemp);

}
