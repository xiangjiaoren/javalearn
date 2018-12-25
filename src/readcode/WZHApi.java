// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   WZHApi.java

package com.ebank.commAdapter.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebank.commAdapter.base.*;
import com.ebank.commAdapter.util.DateUtils;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

// Referenced classes of package com.ebank.commAdapter.api:
//            CommAdapterInit, LfCommClient2, TakePhotoCallback

public final class WZHApi
{
    public static final class Const
    {

        public static String getPdfPath()
        {
            return String.format(PATH_PDF, new Object[] {
                DateUtils.getYYYYMMDD()
            });
        }

        public static String getPhotoPath()
        {
            return String.format(PATH_PHOTO, new Object[] {
                DateUtils.getYYYYMMDD()
            });
        }

        public static String getImgPath()
        {
            return String.format(PATH_GPY_IMG, new Object[] {
                DateUtils.getYYYYMMDD()
            });
        }

        public static String getOnlinePath()
        {
            return String.format(PATH_ONLINE, new Object[] {
                DateUtils.getYYYYMMDD()
            });
        }

        public static final String INIT_SUCCESS = "1";
        public static final String INIT_FAILURE = "0";
        public static final String CONFIRM_IS = "is_confirm";
        public static final String SIGN_PDF = "pdf_path";
        public static final String SIGN_IMG = "img_path";
        public static final String SIGN_DATA = "data_path";
        public static final String SIGN_SVG = "svg_path";
        public static final String UPLOAD_PATH = "path";
        public static final String PATH_PHOTO;
        public static final String PATH_PDF;
        public static final String PATH_GPY_IMG;
        public static final String PATH_ONLINE;

        static 
        {
            PATH_PHOTO = (new StringBuilder()).append(Configuration.STORE_PATH).append("/%s/photo/").toString();
            PATH_PDF = (new StringBuilder()).append(Configuration.STORE_PATH).append("%s/pdf/").toString();
            PATH_GPY_IMG = (new StringBuilder()).append(Configuration.STORE_PATH).append("%s/gpyimg/").toString();
            PATH_ONLINE = (new StringBuilder()).append(Configuration.STORE_PATH).append("%s/online/").toString();
        }

        public Const()
        {
        }
    }


    public WZHApi()
    {
    }

    public static String init(String server, String install, String bankId)
    {
        return CommAdapterInit.init(server, install, bankId);
    }

    public static String init(String server, String install, boolean force)
    {
        return CommAdapterInit.init(server, install, force);
    }

    public static Map sendConfirm(String id, String xml)
    {
        return LfCommClient2.sendConfirmInfo(xml, id);
    }

    public static Map sendSign(String id, String xml)
    {
        return LfCommClient2.sendSignInfo(xml, id);
    }

    public static Map genImg(String xml)
    {
        return LfCommClient2.genImg(xml);
    }

    public static Map genImgFace(String xml)
    {
        return LfCommClient2.genImgFace(xml);
    }

    public static Map genImg(String xml, String path)
    {
        return LfCommClient2.genImg(xml, path);
    }

    public static Map genImgFace(String xml, String path)
    {
        return LfCommClient2.genImgFace(xml, path);
    }

    public static Map genPDF(String id, String xml)
    {
        return LfCommClient2.genPDF(id, xml);
    }

    public static Map upload(String pdfPath, List attachList, String xml, String id)
    {
        return LfCommClient2.uploadCertificateFile(pdfPath, attachList, xml, id);
    }

    public static Map upload(String pdfPath, String dataPath, String svgPath, List attachList, String xml, String id)
    {
        return LfCommClient2.uploadCertificateFile(pdfPath, dataPath, svgPath, attachList, xml, id);
    }

    public static Map markReject(String pdfPath, String mark)
    {
        return LfCommClient2.markReject(pdfPath, mark);
    }

    public static Map mark(String pdfPath, String mark)
    {
        return LfCommClient2.markReject(pdfPath, mark);
    }

    public static Map takePhoto()
    {
        return LfCommClient2.takePhoto(10);
    }

    public static Map takePhoto(int timeout)
    {
        return LfCommClient2.takePhoto(timeout);
    }

    public static Map takePhoto(int timeout, String path)
    {
        return LfCommClient2.takePhoto(timeout, path);
    }

    public static Map previewPhoto(TakePhotoCallback callback)
    {
        return LfCommClient2.previewPhoto(callback);
    }

    public static Map previewPhoto(TakePhotoCallback callback, String path)
    {
        return LfCommClient2.previewPhoto(callback, path);
    }

    public static void closeApp()
    {
        LfCommClient2.closeApp();
    }

    public static Map updateApp()
    {
        return LfCommClient2.updateApp();
    }

    public static void cancleSign(String voice)
    {
        LfCommClient2.cancleSign(voice);
    }

    public static String cancleSignNew(String voice)
    {
        return LfCommClient2.cancleSignNew(voice);
    }

    public static String pushConfirmPdf(File pdfFile)
    {
        return LfCommClient2.pushConfirmPdf(pdfFile);
    }

    public static Map pushSign(File unsignPdf)
    {
        return LfCommClient2.pushSign(unsignPdf);
    }

    public static Map pushSignFinger(File unsignPdf, int timeOut)
    {
        return LfCommClient2.pushSignFinger(unsignPdf, timeOut);
    }

    public static Map goSignFiner(String id, String xml)
    {
        return LfCommClient2.goSignFiner(xml, id);
    }

    public static Map uploadToWZH(String contentPath, byte signData[], byte signSvg[], byte fingerImg[], List attachFileList, String jsonData)
    {
        return LfCommClient2.uploadToWZH(contentPath, signData, signSvg, fingerImg, attachFileList, jsonData);
    }

    public static Map uploadToWZHCart(String contentPath, byte signData[], byte signSvg[], byte fingerImg[], List attachFileList, String jsonData)
    {
        return LfCommClient2.uploadToWZHCart(contentPath, signData, signSvg, fingerImg, attachFileList, jsonData);
    }

    public static long getApkAdvertVersion()
    {
        return LfCommClient2.getVersion("2004");
    }

    public static long getApkAgreementVersion()
    {
        return LfCommClient2.getVersion("2006");
    }

    public static RecEntity evaluate(TellerInfo info)
    {
        return LfCommClient2.evaluate(info);
    }

    public static void main(String args[])
    {
        JSONObject json = new JSONObject();
        json.put("name", "liwbc");
        json.put("password", "123456");
        JSONArray array = new JSONArray();
        JSONObject element1 = new JSONObject();
        element1.put("transId", "3201");
        element1.put("transNo", "12908984375");
        element1.put("amount", "253");
        JSONObject element2 = new JSONObject();
        element2.put("transId", "3202");
        element2.put("transNo", "1324358984375");
        element2.put("amount", "251");
        JSONObject element3 = new JSONObject();
        element3.put("transId", "7031");
        element3.put("transNo", "12903643375");
        element3.put("amount", "123");
        array.add(element1);
        array.add(element2);
        array.add(element3);
        json.put("trans", array);
        System.out.println(json);
        JSONArray array1 = (JSONArray)json.get("trans");
        System.out.println((new StringBuilder()).append("size = ").append(array1.size()).toString());
    }

    public static Boolean isCanCancle = Boolean.valueOf(false);

}
