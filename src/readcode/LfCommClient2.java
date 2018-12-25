// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LfCommClient2.java

package com.ebank.commAdapter.api;

import com.alibaba.fastjson.JSON;
import com.ebank.commAdapter.api.handler.ConvertProccessHandler;
import com.ebank.commAdapter.api.handler.PdfProcess;
import com.ebank.commAdapter.api.handler.PdfProcessHandler;
import com.ebank.commAdapter.api.impl.FingerProcessImpl;
import com.ebank.commAdapter.base.CommAdapterException;
import com.ebank.commAdapter.base.Configuration;
import com.ebank.commAdapter.base.ErrorCodeConfig;
import com.ebank.commAdapter.base.RecEntity;
import com.ebank.commAdapter.base.RetEntity;
import com.ebank.commAdapter.base.SendEntity;
import com.ebank.commAdapter.base.TellerInfo;
import com.ebank.commAdapter.comm.CommClient;
import com.ebank.commAdapter.comm.DefaultAndriodClient;
import com.ebank.commAdapter.comm.hid.LSCHid;
import com.ebank.commAdapter.format.JsonFormat;
import com.ebank.commAdapter.format.impl.DefaultJsonFormat;
import com.ebank.commAdapter.http.HttpUploadFileTemp;
import com.ebank.commAdapter.template.DefaultTemplateDiy;
import com.ebank.commAdapter.update.AndroidUpdate;
import com.ebank.commAdapter.util.Base64ImgUtils;
import com.ebank.commAdapter.util.DateUtils;
import com.ebank.commAdapter.util.FileUtil;
import com.ebank.commAdapter.util.Pdf2ImgUtil;
import com.ebank.commAdapter.util.UngzipBaseUtil;
import com.ebank.commAdapter.util.XmlUtils;
import com.ebank.commAdapter.xml.handler.XmlFormatHandlerFactory;
import com.ebank.commAdapter.xml.handler.XmlUnFormatHandler;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Referenced classes of package com.ebank.commAdapter.api:
//            TakePhotoCallback, IFingerProess, DefaultCommClient, WZHApi

public class LfCommClient2
{
    static final class RetUtil
    {

        public static Map success()
        {
            Map map = new HashMap();
            map.put("erorcd", "AAAAAAAAAA");
            map.put("erortx", "\u6210\u529F");
            return map;
        }

        public static Map cancel()
        {
            Map map = new HashMap();
            map.put("erorcd", "-821");
            map.put("erortx", "\u901A\u4FE1\u88AB\u53D6\u6D88");
            return map;
        }

        public static Map error(Exception e)
        {
            if(e == null)
                return error();
            if(e instanceof CommAdapterException)
            {
                String errMsg = ((CommAdapterException)e).getErrorMessage();
                if(StringUtils.isNotEmpty(errMsg))
                    return error(errMsg);
                String errcode = ((CommAdapterException)e).getErrorCode();
                if(ErrorCodeConfig.getConfigMap().containsKey(errcode))
                {
                    String errMeg = (String)ErrorCodeConfig.getConfigMap().get(errcode);
                    return error(errcode, errMeg);
                }
            }
            return error(e.getMessage());
        }

        public static Map error(String msg)
        {
            if(StringUtils.isEmpty(msg))
                return error();
            else
                return error("1111", msg);
        }

        public static Map error(String code, String msg)
        {
            Map map = new HashMap();
            map.put("erorcd", code);
            map.put("erortx", msg);
            return map;
        }

        public static Map error()
        {
            return error("1111", "\u65E0\u7EB8\u5316\u63D2\u4EF6\u672A\u77E5\u5F02\u5E38");
        }

        public static Map parse(RetEntity retEntity)
        {
            Map map = null;
            if(retEntity.isOk())
            {
                map = success();
                map.putAll(retEntity.getCd());
            } else
            {
                map = error(retEntity.getEc(), retEntity.getEm());
            }
            return map;
        }

        public static final String suc_code = "AAAAAAAAAA";
        public static final String suc_msg = "\u6210\u529F";
        public static final String key_code = "erorcd";
        public static final String key_msg = "erortx";
        public static final String default_err_code = "1111";

        RetUtil()
        {
        }
    }


    public LfCommClient2()
    {
    }

    static Map sendConfirmInfo(String content, String tranId)
    {
        RetEntity ret;
        FileUtil.write((new StringBuilder()).append(confirm_path).append(tranId).toString(), content);
        logger.debug((new StringBuilder()).append("receive teller info:==").append(content).append("||tranId:").append(tranId).toString());
        if(StringUtils.isBlank(content) || StringUtils.isBlank(tranId))
            throw new CommAdapterException("1019");
        XmlUnFormatHandler handler = XmlFormatHandlerFactory.getXmlFormatInstance(tranId);
        if(null == handler)
            handler = XmlFormatHandlerFactory.getDefaultFormatInstance();
        Map paramMap = handler.unformatInputHandler(content, tranId);
        paramMap.put("funcode", "1014");
        paramMap.put("tranId", tranId);
        paramMap.put("jiaoyima", tranId);
        String jsonContent = JSON.toJSONString(paramMap);
        logger.info((new StringBuilder()).append("jsonContent: ").append(jsonContent).toString());
        ret = DefaultCommClient.sendMessage(jsonContent);
        return RetUtil.parse(ret);
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static Map sendSignInfo(String content, String tranId)
    {
        RetEntity re;
        FileUtil.write((new StringBuilder()).append(sign_path).append(tranId).toString(), content);
        logger.debug((new StringBuilder()).append("receive teller info:==").append(content).append("||tranId:").append(tranId).toString());
        if(StringUtils.isBlank(content) || StringUtils.isBlank(tranId))
            throw new CommAdapterException("1019");
        XmlUnFormatHandler handler = XmlFormatHandlerFactory.getXmlFormatInstance(tranId);
        if(null == handler)
            handler = XmlFormatHandlerFactory.getDefaultFormatInstance();
        Map paramMap = handler.unformatPrintHandler(content, false, tranId);
        paramMap.put("tranId", tranId);
        paramMap.put("jiaoyima", tranId);
        paramMap.put("funcode", "1001");
        String jsonContent = JSON.toJSONString(paramMap);
        logger.info((new StringBuilder()).append("jsonContent: ").append(jsonContent).toString());
        re = DefaultCommClient.sendMessage(jsonContent);
        if(re.isCancel())
            return RetUtil.cancel();
        String oper;
        Map retMap;
        if(!re.isOk())
            throw new CommAdapterException(re.getEc(), re.getEm());
        oper = (String)re.getCd().get("operation");
        if(oper == null || !"0".equals(oper))
            break MISSING_BLOCK_LABEL_262;
        retMap = RetUtil.error("3002", "\u5BA2\u6237\u957F\u65F6\u95F4\u672A\u64CD\u4F5C");
        return retMap;
        String signDir;
        String pdfPath;
        String imgPath;
        Map retMap;
        signDir = WZHApi.Const.getPdfPath();
        if(!(new File(signDir)).exists())
            (new File(signDir)).mkdirs();
        byte signPdf[] = (byte[])(byte[])re.getCd().get("content");
        pdfPath = (new StringBuilder()).append(signDir).append(File.separator).append(System.currentTimeMillis()).append("_").append(tranId).append("sign.pdf").toString();
        FileUtil.write(pdfPath, signPdf);
        imgPath = Pdf2ImgUtil.pdf2Png(pdfPath);
        if(oper == null || !"4".equals(oper))
            break MISSING_BLOCK_LABEL_423;
        retMap = RetUtil.error("3001", "\u5BA2\u6237\u62D2\u7EDD\u7B7E\u5B57");
        retMap.put("pdf_path", pdfPath);
        retMap.put("img_path", imgPath);
        return retMap;
        Map returnMap;
        String dataPath = (new StringBuilder()).append(signDir).append(File.separator).append(System.currentTimeMillis()).append("_").append(tranId).append("sign.data").toString();
        byte signData[] = (byte[])(byte[])re.getCd().get("signData");
        FileUtil.write(dataPath, signData);
        byte signSvg[] = (byte[])(byte[])re.getCd().get("signSvg");
        String svgPath = (new StringBuilder()).append(signDir).append(File.separator).append(System.currentTimeMillis()).append("_").append(tranId).append("sign.svg").toString();
        FileUtil.write(svgPath, signSvg);
        returnMap = RetUtil.success();
        returnMap.put("pdf_path", pdfPath);
        returnMap.put("img_path", imgPath);
        returnMap.put("data_path", dataPath);
        returnMap.put("svg_path", svgPath);
        return returnMap;
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static Map genImg(String content)
    {
        Map retMap;
        XmlUnFormatHandler handler = XmlFormatHandlerFactory.getDefaultFormatInstance();
        Map map = handler.unformatPrintHandler(content, false, "8801");
        String busiCode = (String)map.get("busicode");
        String cardId = (String)map.get("outcardid");
        String cardName = (String)map.get("outcardname");
        String issueOffice = (String)map.get("issueoffice");
        String chkResult = (String)map.get("chkresult");
        String cardPhoto = (String)map.get("cardphoto");
        String jiaoyima = (String)map.get("jiaoyima");
        String jiaoyirq = (String)map.get("jiaoyirq");
        String jiaoyijg = (String)map.get("jiaoyijg");
        String jiaoyigy = (String)map.get("jiaoyigy");
        String shoqguiy = (String)map.get("shoqguiy");
        String jiaoyils = (String)map.get("jiaoyils");
        int width = 898;
        int height = 475;
        BufferedImage bufferedImage = new BufferedImage(width, height, 1);
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("\u5B8B\u4F53", 0, 14));
        g.drawString("\u5355\u7B14\u8054\u7F51\u6838\u67E5", 326, 24);
        g.setFont(new Font("\u5B8B\u4F53", 0, 12));
        g.drawString((new StringBuilder()).append("\u4E1A\u52A1\u79CD\u7C7B\uFF1A").append(busiCode).toString(), 98, 85);
        g.drawString((new StringBuilder()).append("\u8EAB\u4EFD\u8BC1\u53F7\uFF1A").append(cardId).toString(), 98, 114);
        g.drawString((new StringBuilder()).append("\u59D3\u540D\uFF1A").append(cardName).toString(), 98, 144);
        g.drawString((new StringBuilder()).append("\u7B7E\u53D1\u673A\u5173\uFF1A").append(issueOffice).toString(), 98, 174);
        g.drawString((new StringBuilder()).append("\u6838\u67E5\u7ED3\u679C\uFF1A").append(chkResult).toString(), 98, 204);
        g.drawString("\u7167\u7247\uFF1A", 490, 85);
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(Base64ImgUtils.toBytes(cardPhoto)));
        g.drawImage(img, 530, 115, 170, 200, null);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u7801\uFF1A").append(jiaoyima).toString(), 90, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u65E5\u671F\uFF1A").append(jiaoyirq).toString(), 298, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u673A\u6784\uFF1A").append(jiaoyijg).toString(), 546, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u67DC\u5458\uFF1A").append(jiaoyigy).toString(), 90, 400);
        g.drawString((new StringBuilder()).append("\u6388\u6743\u67DC\u5458\uFF1A").append(shoqguiy).toString(), 298, 400);
        g.drawString((new StringBuilder()).append("\u67DC\u5458\u6D41\u6C34\uFF1A").append(jiaoyils).toString(), 546, 400);
        String path = (new StringBuilder()).append(WZHApi.Const.getOnlinePath()).append(cardId).append(".jpg").toString();
        logger.debug((new StringBuilder()).append("\u8054\u7F51\u6838\u67E5\u5B58\u5728\u8DEF\u5F84").append(path).toString());
        FileUtil.mkdirs(path);
        File file = new File(path);
        FileUtil.writeJpeg(bufferedImage, file);
        retMap = RetUtil.success();
        retMap.put("img_path", file.getAbsolutePath());
        return retMap;
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static Map genImg(String content, String photoPath)
    {
        logger.debug((new StringBuilder()).append("\u67DC\u9762\u4F20\u8FC7\u6765\u7684\u8054\u7F51\u6838\u67E5\u7684\u8DEF\u5F84").append(photoPath).toString());
        Map retMap;
        XmlUnFormatHandler handler = XmlFormatHandlerFactory.getDefaultFormatInstance();
        Map map = handler.unformatPrintHandler(content, false, "8801");
        String busiCode = (String)map.get("busicode");
        String cardId = (String)map.get("outcardid");
        String cardName = (String)map.get("outcardname");
        String issueOffice = (String)map.get("issueoffice");
        String chkResult = (String)map.get("chkresult");
        String cardPhoto = (String)map.get("cardphoto");
        String jiaoyima = (String)map.get("jiaoyima");
        String jiaoyirq = (String)map.get("jiaoyirq");
        String jiaoyijg = (String)map.get("jiaoyijg");
        String jiaoyigy = (String)map.get("jiaoyigy");
        String shoqguiy = (String)map.get("shoqguiy");
        String jiaoyils = (String)map.get("jiaoyils");
        int width = 898;
        int height = 475;
        BufferedImage bufferedImage = new BufferedImage(width, height, 1);
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("\u5B8B\u4F53", 0, 14));
        g.drawString("\u5355\u7B14\u8054\u7F51\u6838\u67E5", 326, 24);
        g.setFont(new Font("\u5B8B\u4F53", 0, 12));
        g.drawString((new StringBuilder()).append("\u4E1A\u52A1\u79CD\u7C7B\uFF1A").append(busiCode).toString(), 98, 85);
        g.drawString((new StringBuilder()).append("\u8EAB\u4EFD\u8BC1\u53F7\uFF1A").append(cardId).toString(), 98, 114);
        g.drawString((new StringBuilder()).append("\u59D3\u540D\uFF1A").append(cardName).toString(), 98, 144);
        g.drawString((new StringBuilder()).append("\u7B7E\u53D1\u673A\u5173\uFF1A").append(issueOffice).toString(), 98, 174);
        g.drawString((new StringBuilder()).append("\u6838\u67E5\u7ED3\u679C\uFF1A").append(chkResult).toString(), 98, 204);
        g.drawString("\u7167\u7247\uFF1A", 490, 85);
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(Base64ImgUtils.toBytes(cardPhoto)));
        g.drawImage(img, 530, 115, 170, 200, null);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u7801\uFF1A").append(jiaoyima).toString(), 90, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u65E5\u671F\uFF1A").append(jiaoyirq).toString(), 298, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u673A\u6784\uFF1A").append(jiaoyijg).toString(), 546, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u67DC\u5458\uFF1A").append(jiaoyigy).toString(), 90, 400);
        g.drawString((new StringBuilder()).append("\u6388\u6743\u67DC\u5458\uFF1A").append(shoqguiy).toString(), 298, 400);
        g.drawString((new StringBuilder()).append("\u67DC\u5458\u6D41\u6C34\uFF1A").append(jiaoyils).toString(), 546, 400);
        String path = (new StringBuilder()).append(photoPath).append(cardId).append(".jpg").toString();
        logger.debug((new StringBuilder()).append("\u67DC\u9762\u4F20\u8FC7\u6765\u7684\u8054\u7F51\u6838\u67E5\u5B58\u653E\u7684\u8DEF\u5F84").append(path).toString());
        FileUtil.mkdirs(path);
        File file = new File(path);
        FileUtil.writeJpeg(bufferedImage, file);
        retMap = RetUtil.success();
        retMap.put("img_path", file.getAbsolutePath());
        return retMap;
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static Map genImgFace(String content)
    {
        Map retMap;
        XmlUnFormatHandler handler = XmlFormatHandlerFactory.getDefaultFormatInstance();
        Map map = handler.unformatPrintHandler(content, false, "8806");
        String busiCode = (String)map.get("busicode");
        String cardId = (String)map.get("outcardid");
        String cardPhoto = (String)map.get("cardphoto");
        String cardName = (String)map.get("outcardname");
        String scenefacecheckresult = (String)map.get("scenefacecheckresult");
        String netfacecheckresult = (String)map.get("netfacecheckresult");
        String checkresult = (String)map.get("checkresult");
        String jiaoyima = (String)map.get("jiaoyima");
        String jiaoyirq = (String)map.get("jiaoyirq");
        String jiaoyijg = (String)map.get("jiaoyijg");
        String jiaoyigy = (String)map.get("jiaoyigy");
        String shoqguiy = (String)map.get("shoqguiy");
        String jiaoyils = (String)map.get("jiaoyils");
        int width = 898;
        int height = 475;
        BufferedImage bufferedImage = new BufferedImage(width, height, 1);
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("\u5B8B\u4F53", 0, 14));
        g.drawString("\u4EBA\u8138\u8BC6\u522B\u7ED3\u679C", 326, 24);
        g.setFont(new Font("\u5B8B\u4F53", 0, 12));
        g.drawString((new StringBuilder()).append("\u4E1A\u52A1\u79CD\u7C7B\uFF1A").append(busiCode).toString(), 98, 85);
        g.drawString((new StringBuilder()).append("\u8EAB\u4EFD\u8BC1\u53F7\uFF1A").append(cardId).toString(), 98, 114);
        g.drawString((new StringBuilder()).append("\u59D3\u540D\uFF1A").append(cardName).toString(), 98, 144);
        g.drawString((new StringBuilder()).append("\u8BC1\u4EF6-\u73B0\u573A\u8BC6\u522B\u7ED3\u679C\uFF1A").append(scenefacecheckresult).toString(), 98, 174);
        g.drawString((new StringBuilder()).append("\u8054\u7F51-\u73B0\u573A\u8BC6\u522B\u7ED3\u679C\uFF1A").append(netfacecheckresult).toString(), 98, 204);
        g.drawString("\u7167\u7247\uFF1A", 490, 85);
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(Base64ImgUtils.toBytes(cardPhoto)));
        g.drawImage(img, 530, 115, 170, 200, null);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u7801\uFF1A").append(jiaoyima).toString(), 90, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u65E5\u671F\uFF1A").append(jiaoyirq).toString(), 298, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u673A\u6784\uFF1A").append(jiaoyijg).toString(), 546, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u67DC\u5458\uFF1A").append(jiaoyigy).toString(), 90, 400);
        g.drawString((new StringBuilder()).append("\u6388\u6743\u67DC\u5458\uFF1A").append(shoqguiy).toString(), 298, 400);
        g.drawString((new StringBuilder()).append("\u67DC\u5458\u6D41\u6C34\uFF1A").append(jiaoyils).toString(), 546, 400);
        String path = (new StringBuilder()).append(WZHApi.Const.getOnlinePath()).append(cardId).append("_face").append(".jpg").toString();
        logger.debug((new StringBuilder()).append("\u4EBA\u8138\u8BC6\u522B\u56FE\u7247\u5B58\u653E\u7684\u8DEF\u5F84").append(path).toString());
        FileUtil.mkdirs(path);
        File file = new File(path);
        FileUtil.writeJpeg(bufferedImage, file);
        retMap = RetUtil.success();
        retMap.put("img_path", file.getAbsolutePath());
        return retMap;
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static Map genImgFace(String content, String photoPath)
    {
        logger.debug((new StringBuilder()).append("\u67DC\u9762\u4F20\u8FC7\u6765\u7684\u4EBA\u8138\u8BC6\u522B\u7684\u8DEF\u5F84").append(photoPath).toString());
        Map retMap;
        XmlUnFormatHandler handler = XmlFormatHandlerFactory.getDefaultFormatInstance();
        Map map = handler.unformatPrintHandler(content, false, "8806");
        String busiCode = (String)map.get("busicode");
        String cardId = (String)map.get("outcardid");
        String cardPhoto = (String)map.get("cardphoto");
        String cardName = (String)map.get("outcardname");
        String scenefacecheckresult = (String)map.get("scenefacecheckresult");
        String netfacecheckresult = (String)map.get("netfacecheckresult");
        String checkresult = (String)map.get("checkresult");
        String jiaoyima = (String)map.get("jiaoyima");
        String jiaoyirq = (String)map.get("jiaoyirq");
        String jiaoyijg = (String)map.get("jiaoyijg");
        String jiaoyigy = (String)map.get("jiaoyigy");
        String shoqguiy = (String)map.get("shoqguiy");
        String jiaoyils = (String)map.get("jiaoyils");
        int width = 898;
        int height = 475;
        BufferedImage bufferedImage = new BufferedImage(width, height, 1);
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("\u5B8B\u4F53", 0, 14));
        g.drawString("\u4EBA\u8138\u8BC6\u522B\u7ED3\u679C", 326, 24);
        g.setFont(new Font("\u5B8B\u4F53", 0, 12));
        g.drawString((new StringBuilder()).append("\u4E1A\u52A1\u79CD\u7C7B\uFF1A").append(busiCode).toString(), 98, 85);
        g.drawString((new StringBuilder()).append("\u8EAB\u4EFD\u8BC1\u53F7\uFF1A").append(cardId).toString(), 98, 114);
        g.drawString((new StringBuilder()).append("\u59D3\u540D\uFF1A").append(cardName).toString(), 98, 144);
        g.drawString((new StringBuilder()).append("\u8BC1\u4EF6-\u73B0\u573A\u8BC6\u522B\u7ED3\u679C\uFF1A").append(scenefacecheckresult).toString(), 98, 174);
        g.drawString((new StringBuilder()).append("\u8054\u7F51-\u73B0\u573A\u8BC6\u522B\u7ED3\u679C\uFF1A").append(netfacecheckresult).toString(), 98, 204);
        g.drawString("\u7167\u7247\uFF1A", 490, 85);
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(Base64ImgUtils.toBytes(cardPhoto)));
        g.drawImage(img, 530, 115, 170, 200, null);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u7801\uFF1A").append(jiaoyima).toString(), 90, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u65E5\u671F\uFF1A").append(jiaoyirq).toString(), 298, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u673A\u6784\uFF1A").append(jiaoyijg).toString(), 546, 384);
        g.drawString((new StringBuilder()).append("\u4EA4\u6613\u67DC\u5458\uFF1A").append(jiaoyigy).toString(), 90, 400);
        g.drawString((new StringBuilder()).append("\u6388\u6743\u67DC\u5458\uFF1A").append(shoqguiy).toString(), 298, 400);
        g.drawString((new StringBuilder()).append("\u67DC\u5458\u6D41\u6C34\uFF1A").append(jiaoyils).toString(), 546, 400);
        String path = (new StringBuilder()).append(photoPath).append(cardId).append("_face").append(".jpg").toString();
        logger.debug((new StringBuilder()).append("\u67DC\u9762\u4F20\u8FC7\u6765\u7684\u4EBA\u8138\u8BC6\u522B\u5B58\u5728\u8DEF\u5F84").append(path).toString());
        FileUtil.mkdirs(path);
        File file = new File(path);
        FileUtil.writeJpeg(bufferedImage, file);
        retMap = RetUtil.success();
        retMap.put("img_path", file.getAbsolutePath());
        return retMap;
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static Map genPDF(String tranId, String content)
    {
        String funcode;
        XmlUnFormatHandler handler;
        funcode = "1001";
        logger.debug((new StringBuilder()).append("receive teller info:==").append(content).append("||tranId:").append(tranId).toString());
        if(StringUtils.isBlank(content) || StringUtils.isBlank(tranId))
            return RetUtil.error("1019", "\u53C2\u6570\u4E3A\u7A7A");
        handler = XmlFormatHandlerFactory.getXmlFormatInstance(tranId);
        if(null == handler)
            handler = XmlFormatHandlerFactory.getDefaultFormatInstance();
        Map paramMap = null;
        byte send[];
        Map paramMap = handler.unformatPrintHandler(content, false, tranId);
        paramMap.put("tranId", tranId);
        paramMap.put("jiaoyima", tranId);
        paramMap.put("funcode", "1001");
        SendEntity se = new SendEntity();
        se.setFuncode(funcode);
        send = DefaultTemplateDiy.generPdfByTemp(paramMap, se);
        if(send == null)
            return RetUtil.error("1021", "\u6A21\u677F\u672A\u627E\u5230");
        Map retMap;
        String path = String.format(WZHApi.Const.PATH_PDF, new Object[] {
            DateUtils.getYYYYMMDD()
        });
        String pdfPath = (new StringBuilder()).append(path).append("/").append(tranId).append("_gen.pdf").toString();
        FileUtil.write(pdfPath, send);
        retMap = RetUtil.success();
        retMap.put("pdf_path", pdfPath);
        return retMap;
        Exception e;
        e;
        logger.error("", e);
        return RetUtil.error(e);
    }

    static Map takePhoto(int timeout)
    {
        RetEntity rt;
        Map retMap;
        Map params = new HashMap();
        params.put("funcode", "1011");
        String json = JSON.toJSONString(params);
        rt = DefaultCommClient.sendMessage(json, timeout);
        if(!rt.isOk())
            break MISSING_BLOCK_LABEL_219;
        byte photoData[] = (byte[])(byte[])rt.getCd().get("photo");
        String photoPath = String.format(WZHApi.Const.PATH_PHOTO, new Object[] {
            DateUtils.getYYYYMMDD()
        });
        logger.info((new StringBuilder()).append("photoPath: ").append(photoPath).toString());
        File path = new File(photoPath);
        if(!path.exists())
            path.mkdirs();
        String photoName = (new StringBuilder()).append(photoPath).append("/").append(System.currentTimeMillis()).append(".jpg").toString();
        File f = new File(photoName);
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(photoData);
        fos.flush();
        fos.close();
        retMap = RetUtil.success();
        retMap.put("photoPath", photoName);
        return retMap;
        return RetUtil.error(rt.getEc(), rt.getEm());
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static Map takePhoto(int timeout, String photoPath)
    {
        RetEntity rt;
        Map retMap;
        Map params = new HashMap();
        params.put("funcode", "1011");
        String json = JSON.toJSONString(params);
        rt = DefaultCommClient.sendMessage(json, timeout);
        if(!rt.isOk())
            break MISSING_BLOCK_LABEL_229;
        byte photoData[] = (byte[])(byte[])rt.getCd().get("photo");
        logger.info((new StringBuilder()).append("photoPath: ").append(photoPath).toString());
        File path = new File(photoPath);
        if(!path.exists())
            path.mkdirs();
        String photoName = (new StringBuilder()).append(photoPath).append("/").append(System.currentTimeMillis()).append(".jpg").toString();
        logger.info((new StringBuilder()).append("\u62CD\u7167\u540E\u7167\u7247\u6240\u5728\u8DEF\u5F84: ").append(photoName).toString());
        File f = new File(photoName);
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(photoData);
        fos.flush();
        fos.close();
        retMap = RetUtil.success();
        retMap.put("photoPath", photoName);
        return retMap;
        return RetUtil.error(rt.getEc(), rt.getEm());
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static void closeApp()
    {
        try
        {
            Map params = new HashMap();
            params.put("funcode", "4004");
            String jsonStr = JSON.toJSONString(params);
            DefaultCommClient.sendMessage(jsonStr, 10);
        }
        catch(Exception e)
        {
            logger.error(" ", e);
        }
    }

    static Map updateApp()
    {
        RecEntity re;
        Map vMap = getAppVersion();
        String versionCode = (String)vMap.get("versionCode");
        versionCode = StringUtils.isEmpty(versionCode) ? "0" : versionCode;
        re = AndroidUpdate.checkUpdate(true, versionCode);
        if(re != null && "0000".equals(re.getResultcode()))
            return RetUtil.error(re.getResultcode(), "\u67DC\u5916\u6E05\u8FD4\u56DE\u5931\u8D25");
        Map retMap = RetUtil.success();
        return retMap;
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    public static Map getAppVersion()
    {
        RecEntity re;
        SendEntity sendEntity = new SendEntity();
        sendEntity.setFuncode("1017");
        re = DefaultCommClient.sendMessage(JSON.toJSONString(sendEntity).getBytes());
        logger.info((new StringBuilder()).append("re\uFF1A").append(re).toString());
        if(!"0000".equals(re.getResultcode()))
            return RetUtil.error(re.getResultcode(), "\u67DC\u5916\u6E05\u8FD4\u56DE\u5931\u8D25");
        Map retMap;
        String versionCode = (String)re.getContent().get("content");
        retMap = RetUtil.success();
        retMap.put("versionCode", versionCode);
        return retMap;
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static Map uploadCertificateFile(String contentPath, java.util.List attachFileList, String xmlContent, String tranId)
    {
        Map returnMap;
        logger.debug((new StringBuilder()).append("receive teller info:==").append(xmlContent).append("||tranId:").append(tranId).append("||contentPath=").append(contentPath).toString());
        if(StringUtils.isBlank(xmlContent) || StringUtils.isBlank(tranId) || StringUtils.isBlank(contentPath))
            throw new CommAdapterException("1019");
        File content = new File(contentPath);
        byte content_buff[] = null;
        if(content.exists())
        {
            FileInputStream fis = new FileInputStream(content);
            if(fis != null)
            {
                int length = fis.available();
                content_buff = new byte[length];
                fis.read(content_buff);
            }
            fis.close();
        } else
        {
            throw new CommAdapterException("1023");
        }
        if(xmlContent.indexOf("<") != -1 && xmlContent.lastIndexOf(">") != -1 && xmlContent.lastIndexOf(">") > xmlContent.indexOf("<"))
            xmlContent = xmlContent.substring(xmlContent.indexOf("<"), xmlContent.lastIndexOf(">") + 1);
        XmlUnFormatHandler handler = XmlFormatHandlerFactory.getXmlFormatInstance(tranId);
        if(null == handler)
            handler = XmlFormatHandlerFactory.getDefaultFormatInstance();
        Map paramMap = handler.unformatPrintHandler(xmlContent, true, tranId);
        paramMap.put("jiaoyima", tranId);
        String jsonContent = JSON.toJSONString(paramMap);
        String path = HttpUploadFileTemp.postFile(content_buff, null, null, paramMap, attachFileList, null, jsonContent);
        returnMap = RetUtil.success();
        returnMap.put("path", path);
        return returnMap;
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static Map uploadCertificateFile(String contentPath, String dataPath, String svgPath, java.util.List attachFileList, String xmlContent, String tranId)
    {
        Map returnMap;
        logger.debug((new StringBuilder()).append("receive teller info:==").append(xmlContent).append("||tranId:").append(tranId).append("||contentPath=").append(contentPath).toString());
        if(StringUtils.isBlank(xmlContent) || StringUtils.isBlank(tranId) || StringUtils.isBlank(contentPath))
            throw new CommAdapterException("1019");
        byte contentBuff[] = FileUtil.getData(contentPath);
        if(contentBuff == null)
            throw new CommAdapterException("1003");
        byte dataBuff[] = FileUtil.getData(dataPath);
        byte svgBuff[] = FileUtil.getData(svgPath);
        XmlUnFormatHandler handler = XmlFormatHandlerFactory.getXmlFormatInstance(tranId);
        if(null == handler)
            handler = XmlFormatHandlerFactory.getDefaultFormatInstance();
        Map paramMap = handler.unformatPrintHandler(XmlUtils.trim(xmlContent), true, tranId);
        paramMap.put("jiaoyima", tranId);
        String jsonContent = JSON.toJSONString(paramMap);
        String path = HttpUploadFileTemp.postFile(contentBuff, dataBuff, svgBuff, paramMap, attachFileList, null, jsonContent);
        returnMap = RetUtil.success();
        returnMap.put("path", path);
        return returnMap;
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static Map markReject(String pdfPath, String mark)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String markPath = (new StringBuilder()).append(FileUtil.getName(pdfPath)).append("_mark").append(FileUtil.getDotSuffix(pdfPath)).toString();
        try
        {
            PdfReader pdfReader = new PdfReader(pdfPath);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);
            PdfContentByte pdfContentByte = pdfStamper.getOverContent(pdfReader.getNumberOfPages());
            Rectangle rectangle = pdfReader.getPageSize(pdfReader.getNumberOfPages());
            int height = (int)rectangle.getHeight();
            pdfContentByte.beginText();
            pdfContentByte.setColorFill(BaseColor.RED);
            String fontPath = (new StringBuilder()).append(Configuration.ROOT_PATH).append("/simsun.ttf").toString();
            BaseFont font = BaseFont.createFont(fontPath, "Identity-H", true);
            pdfContentByte.setFontAndSize(font, 12F);
            pdfContentByte.setTextMatrix(0.0F, 0.0F);
            int line_len = 45;
            if(mark.length() > line_len)
            {
                pdfContentByte.showTextAligned(0, mark.substring(0, line_len), 10F, height - 15, 0.0F);
                pdfContentByte.showTextAligned(0, mark.substring(line_len), 10F, height - 30, 0.0F);
            } else
            {
                pdfContentByte.showTextAligned(0, mark, 10F, height - 15, 0.0F);
            }
            pdfContentByte.endText();
            pdfStamper.close();
            pdfReader.close();
            FileUtil.write(markPath, baos.toByteArray());
        }
        catch(Exception e)
        {
            return RetUtil.error(e);
        }
        String imgPath = Pdf2ImgUtil.pdf2Png(markPath);
        Map retMap = RetUtil.success();
        retMap.put("pdf_path", markPath);
        retMap.put("img_path", imgPath);
        return retMap;
    }

    static Map previewPhoto(TakePhotoCallback callback)
    {
        try
        {
            JFrame jFrame = new JFrame("\u62CD\u7167\u9884\u89C8");
            jFrame.setUndecorated(true);
            jFrame.setLayout(new BorderLayout());
            JPanel bodyPanel = new JPanel();
            bodyPanel.setLayout(null);
            jFrame.setSize(660, 500);
            jFrame.setBackground(Color.GRAY);
            JLabel jLabel = new JLabel();
            jLabel.setBounds(0, 5, 660, 435);
            jLabel.setHorizontalAlignment(0);
            CommClient client = DefaultAndriodClient.getCommClient();
            JButton takeBtn = new JButton("\u62CD\u7167");
            takeBtn.setEnabled(false);
            isBtnEnable = false;
            ActionListener takeListener = new ActionListener(jFrame, jLabel, client, callback) {

                public void actionPerformed(ActionEvent event)
                {
                    File file;
                    LfCommClient2.isPreview = false;
                    LfCommClient2.isBtnEnable = false;
                    LfCommClient2.logger.debug("\u60A8\u70B9\u51FB\u4E86\u62CD\u7167\u6309\u94AE");
                    LfCommClient2.logger.debug((new StringBuilder()).append("isBtnEnable: ").append(LfCommClient2.isBtnEnable).toString());
                    EventQueue.invokeLater(new Runnable() {

                        public void run()
                        {
                            LfCommClient2.logger.debug("invokeLater...");
                            try
                            {
                                Thread.sleep(500L);
                            }
                            catch(InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            LfCommClient2.logger.debug("sleep 500 finish...");
                            jFrame.setVisible(false);
                            jFrame.dispose();
                        }

                        final _cls1 this$0;

                    
                    {
                        this$0 = _cls1.this;
                        super();
                    }
                    });
                    file = null;
                    String path = String.format(WZHApi.Const.PATH_PHOTO, new Object[] {
                        DateUtils.getYYYYMMDD()
                    });
                    File filePath = new File(path);
                    if(!filePath.exists())
                        filePath.mkdirs();
                    file = new File(filePath, (new StringBuilder()).append(System.currentTimeMillis()).append(".jpg").toString());
                    ImageIcon imageIcon = (ImageIcon)jLabel.getIcon();
                    Image image = imageIcon.getImage();
                    BufferedImage bImg = LfCommClient2.image2(image);
                    FileUtil.writeJpeg(bImg, file);
                    LSCHid.USB_Cancel();
                    SendEntity sendEntity = new SendEntity();
                    sendEntity.setFuncode("1018");
                    client.send(JSON.toJSONString(sendEntity).getBytes());
                    if(callback != null)
                        callback.takePhoto(file != null ? file.getAbsolutePath() : "");
                    EventQueue.invokeLater(new Runnable() {

                        public void run()
                        {
                            jFrame.setVisible(false);
                            jFrame.dispose();
                        }

                        final _cls1 this$0;

                    
                    {
                        this$0 = _cls1.this;
                        super();
                    }
                    });
                    break MISSING_BLOCK_LABEL_385;
                    Exception e;
                    e;
                    if(callback != null)
                        callback.takePhoto(file != null ? file.getAbsolutePath() : "");
                    EventQueue.invokeLater(new Runnable() {

                        public void run()
                        {
                            jFrame.setVisible(false);
                            jFrame.dispose();
                        }

                        final _cls1 this$0;

                    
                    {
                        this$0 = _cls1.this;
                        super();
                    }
                    });
                    LfCommClient2.logger.error("", e);
                    if(callback != null)
                        callback.takePhoto(file != null ? file.getAbsolutePath() : "");
                    EventQueue.invokeLater(new _cls3());
                    break MISSING_BLOCK_LABEL_385;
                    Exception exception;
                    exception;
                    if(callback != null)
                        callback.takePhoto(file != null ? file.getAbsolutePath() : "");
                    EventQueue.invokeLater(new _cls3());
                    throw exception;
                }

                final JFrame val$jFrame;
                final JLabel val$jLabel;
                final CommClient val$client;
                final TakePhotoCallback val$callback;

            
            {
                jFrame = jframe;
                jLabel = jlabel;
                client = commclient;
                callback = takephotocallback;
                super();
            }
            };
            takeBtn.registerKeyboardAction(takeListener, KeyStroke.getKeyStroke(10, 0), 2);
            takeBtn.addActionListener(takeListener);
            if(!isPreview)
            {
                isPreview = true;
                SendEntity sendEntity = new SendEntity();
                sendEntity.setFuncode("1019");
                client.send(JSON.toJSONString(sendEntity).getBytes());
                (new Thread(new Runnable(client, jLabel, takeBtn) {

                    public void run()
                    {
                        do
                        {
                            if(!LfCommClient2.isPreview)
                                break;
                            try
                            {
                                byte data[] = client.read();
                                ImageIcon imageIcon = new ImageIcon(data);
                                jLabel.setIcon(imageIcon);
                                if(!LfCommClient2.isBtnEnable)
                                {
                                    LfCommClient2.logger.debug((new StringBuilder()).append("isBtnEnable: ").append(LfCommClient2.isBtnEnable).toString());
                                    takeBtn.setEnabled(true);
                                    LfCommClient2.isBtnEnable = true;
                                    LfCommClient2.logger.debug((new StringBuilder()).append("isBtnEnable: ").append(LfCommClient2.isBtnEnable).toString());
                                }
                            }
                            catch(CommAdapterException e)
                            {
                                LfCommClient2.logger.warn((new StringBuilder()).append("CommAdapterException ").append(e.getErrorCode()).append("|").append(e.getErrorMessage()).toString());
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        } while(true);
                    }

                    final CommClient val$client;
                    final JLabel val$jLabel;
                    final JButton val$takeBtn;

            
            {
                client = commclient;
                jLabel = jlabel;
                takeBtn = jbutton;
                super();
            }
                })).start();
            }
            JPanel jPanel = new JPanel();
            JButton closeBtn = new JButton("\u5173\u95ED");
            closeBtn.addActionListener(new ActionListener(jFrame, callback, client) {

                public void actionPerformed(ActionEvent e)
                {
                    LfCommClient2.isPreview = false;
                    LfCommClient2.isBtnEnable = false;
                    LfCommClient2.logger.debug("\u60A8\u70B9\u51FB\u4E86\u5173\u95ED\u6309\u94AE...");
                    LfCommClient2.logger.debug((new StringBuilder()).append("isBtnEnable: ").append(LfCommClient2.isBtnEnable).toString());
                    jFrame.setVisible(false);
                    jFrame.dispose();
                    if(callback != null)
                        callback.closePreview();
                    LSCHid.USB_Cancel();
                    SendEntity sendEntity = new SendEntity();
                    sendEntity.setFuncode("1018");
                    try
                    {
                        client.send(JSON.toJSONString(sendEntity).getBytes());
                    }
                    catch(CommAdapterException e2)
                    {
                        e2.printStackTrace();
                    }
                }

                final JFrame val$jFrame;
                final TakePhotoCallback val$callback;
                final CommClient val$client;

            
            {
                jFrame = jframe;
                callback = takephotocallback;
                client = commclient;
                super();
            }
            });
            bodyPanel.setBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.GRAY));
            takeBtn.setBounds(200, 460, 100, 30);
            closeBtn.setBounds(330, 460, 100, 30);
            bodyPanel.add(jLabel);
            bodyPanel.add(takeBtn);
            bodyPanel.add(closeBtn);
            jFrame.add(bodyPanel);
            jFrame.setDefaultCloseOperation(2);
            jFrame.setLocationRelativeTo(null);
            jFrame.setAlwaysOnTop(true);
            jFrame.setVisible(true);
        }
        catch(Exception e)
        {
            return RetUtil.error(e);
        }
        return RetUtil.success();
    }

    static Map previewPhoto(TakePhotoCallback callback, String path)
    {
        try
        {
            JFrame jFrame = new JFrame("\u62CD\u7167\u9884\u89C8");
            jFrame.setUndecorated(true);
            jFrame.setLayout(new BorderLayout());
            JPanel bodyPanel = new JPanel();
            bodyPanel.setLayout(null);
            jFrame.setSize(660, 500);
            jFrame.setBackground(Color.GRAY);
            JLabel jLabel = new JLabel();
            jLabel.setBounds(0, 5, 660, 435);
            jLabel.setHorizontalAlignment(0);
            CommClient client = DefaultAndriodClient.getCommClient();
            JButton takeBtn = new JButton("\u62CD\u7167");
            takeBtn.setEnabled(false);
            isBtnEnable = false;
            ActionListener takeListener = new ActionListener(jFrame, path, jLabel, client, callback) {

                public void actionPerformed(ActionEvent event)
                {
                    File file;
                    LfCommClient2.isPreview = false;
                    LfCommClient2.isBtnEnable = false;
                    LfCommClient2.logger.debug("\u60A8\u70B9\u51FB\u4E86\u62CD\u7167\u6309\u94AE");
                    LfCommClient2.logger.debug((new StringBuilder()).append("isBtnEnable: ").append(LfCommClient2.isBtnEnable).toString());
                    EventQueue.invokeLater(new Runnable() {

                        public void run()
                        {
                            LfCommClient2.logger.debug("invokeLater...");
                            try
                            {
                                Thread.sleep(500L);
                            }
                            catch(InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            LfCommClient2.logger.debug("sleep 500 finish...");
                            jFrame.setVisible(false);
                            jFrame.dispose();
                        }

                        final _cls4 this$0;

                    
                    {
                        this$0 = _cls4.this;
                        super();
                    }
                    });
                    file = null;
                    File filePath = new File(path);
                    if(!filePath.exists())
                        filePath.mkdirs();
                    file = new File(filePath, (new StringBuilder()).append(System.currentTimeMillis()).append(".jpg").toString());
                    ImageIcon imageIcon = (ImageIcon)jLabel.getIcon();
                    Image image = imageIcon.getImage();
                    BufferedImage bImg = LfCommClient2.image2(image);
                    FileUtil.writeJpeg(bImg, file);
                    LSCHid.USB_Cancel();
                    SendEntity sendEntity = new SendEntity();
                    sendEntity.setFuncode("1018");
                    client.send(JSON.toJSONString(sendEntity).getBytes());
                    if(callback != null)
                        callback.takePhoto(file != null ? file.getAbsolutePath() : "");
                    EventQueue.invokeLater(new Runnable() {

                        public void run()
                        {
                            jFrame.setVisible(false);
                            jFrame.dispose();
                        }

                        final _cls4 this$0;

                    
                    {
                        this$0 = _cls4.this;
                        super();
                    }
                    });
                    break MISSING_BLOCK_LABEL_367;
                    Exception e;
                    e;
                    if(callback != null)
                        callback.takePhoto(file != null ? file.getAbsolutePath() : "");
                    EventQueue.invokeLater(new Runnable() {

                        public void run()
                        {
                            jFrame.setVisible(false);
                            jFrame.dispose();
                        }

                        final _cls4 this$0;

                    
                    {
                        this$0 = _cls4.this;
                        super();
                    }
                    });
                    LfCommClient2.logger.error("", e);
                    if(callback != null)
                        callback.takePhoto(file != null ? file.getAbsolutePath() : "");
                    EventQueue.invokeLater(new _cls3());
                    break MISSING_BLOCK_LABEL_367;
                    Exception exception;
                    exception;
                    if(callback != null)
                        callback.takePhoto(file != null ? file.getAbsolutePath() : "");
                    EventQueue.invokeLater(new _cls3());
                    throw exception;
                }

                final JFrame val$jFrame;
                final String val$path;
                final JLabel val$jLabel;
                final CommClient val$client;
                final TakePhotoCallback val$callback;

            
            {
                jFrame = jframe;
                path = s;
                jLabel = jlabel;
                client = commclient;
                callback = takephotocallback;
                super();
            }
            };
            takeBtn.registerKeyboardAction(takeListener, KeyStroke.getKeyStroke(10, 0), 2);
            takeBtn.addActionListener(takeListener);
            if(!isPreview)
            {
                isPreview = true;
                SendEntity sendEntity = new SendEntity();
                sendEntity.setFuncode("1019");
                client.send(JSON.toJSONString(sendEntity).getBytes());
                (new Thread(new Runnable(client, jLabel, takeBtn) {

                    public void run()
                    {
                        do
                        {
                            if(!LfCommClient2.isPreview)
                                break;
                            try
                            {
                                byte data[] = client.read();
                                ImageIcon imageIcon = new ImageIcon(data);
                                jLabel.setIcon(imageIcon);
                                if(!LfCommClient2.isBtnEnable)
                                {
                                    LfCommClient2.logger.debug((new StringBuilder()).append("isBtnEnable: ").append(LfCommClient2.isBtnEnable).toString());
                                    takeBtn.setEnabled(true);
                                    LfCommClient2.isBtnEnable = true;
                                    LfCommClient2.logger.debug((new StringBuilder()).append("isBtnEnable: ").append(LfCommClient2.isBtnEnable).toString());
                                }
                            }
                            catch(CommAdapterException e)
                            {
                                LfCommClient2.logger.warn((new StringBuilder()).append("CommAdapterException ").append(e.getErrorCode()).append("|").append(e.getErrorMessage()).toString());
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        } while(true);
                    }

                    final CommClient val$client;
                    final JLabel val$jLabel;
                    final JButton val$takeBtn;

            
            {
                client = commclient;
                jLabel = jlabel;
                takeBtn = jbutton;
                super();
            }
                })).start();
            }
            JPanel jPanel = new JPanel();
            JButton closeBtn = new JButton("\u5173\u95ED");
            closeBtn.addActionListener(new ActionListener(jFrame, callback, client) {

                public void actionPerformed(ActionEvent e)
                {
                    LfCommClient2.isPreview = false;
                    LfCommClient2.isBtnEnable = false;
                    LfCommClient2.logger.debug("\u60A8\u70B9\u51FB\u4E86\u5173\u95ED\u6309\u94AE...");
                    LfCommClient2.logger.debug((new StringBuilder()).append("isBtnEnable: ").append(LfCommClient2.isBtnEnable).toString());
                    jFrame.setVisible(false);
                    jFrame.dispose();
                    if(callback != null)
                        callback.closePreview();
                    LSCHid.USB_Cancel();
                    SendEntity sendEntity = new SendEntity();
                    sendEntity.setFuncode("1018");
                    try
                    {
                        client.send(JSON.toJSONString(sendEntity).getBytes());
                    }
                    catch(CommAdapterException e2)
                    {
                        e2.printStackTrace();
                    }
                }

                final JFrame val$jFrame;
                final TakePhotoCallback val$callback;
                final CommClient val$client;

            
            {
                jFrame = jframe;
                callback = takephotocallback;
                client = commclient;
                super();
            }
            });
            bodyPanel.setBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.GRAY));
            takeBtn.setBounds(200, 460, 100, 30);
            closeBtn.setBounds(330, 460, 100, 30);
            bodyPanel.add(jLabel);
            bodyPanel.add(takeBtn);
            bodyPanel.add(closeBtn);
            jFrame.add(bodyPanel);
            jFrame.setDefaultCloseOperation(2);
            jFrame.setLocationRelativeTo(null);
            jFrame.setAlwaysOnTop(true);
            jFrame.setVisible(true);
        }
        catch(Exception e)
        {
            return RetUtil.error(e);
        }
        return RetUtil.success();
    }

    private static void preview(CommClient client, JLabel jLabel)
        throws CommAdapterException
    {
        if(isPreview)
        {
            return;
        } else
        {
            isPreview = true;
            SendEntity sendEntity = new SendEntity();
            sendEntity.setFuncode("1019");
            client.send(JSON.toJSONString(sendEntity).getBytes());
            (new Thread(new Runnable(client, jLabel) {

                public void run()
                {
                    while(LfCommClient2.isPreview) 
                        try
                        {
                            byte data[] = client.read();
                            ImageIcon imageIcon = new ImageIcon(data);
                            jLabel.setIcon(imageIcon);
                        }
                        catch(CommAdapterException e)
                        {
                            e.printStackTrace();
                        }
                }

                final CommClient val$client;
                final JLabel val$jLabel;

            
            {
                client = commclient;
                jLabel = jlabel;
                super();
            }
            })).start();
            return;
        }
    }

    public static BufferedImage image2(Image img)
    {
        long time = System.currentTimeMillis();
        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), 1);
        Graphics g = bi.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        System.out.println((new StringBuilder()).append("img transfer time: ").append(System.currentTimeMillis() - time).toString());
        return bi;
    }

    public static void cancleSign(String voice)
    {
        try
        {
            int i = LSCHid.USB_Cancel();
            logger.info((new StringBuilder()).append("\u53D6\u6D88\u6307\u4EE4,\u8FD4\u56DE\u503C : ").append(i).toString());
            FingerProcessImpl.setIsCancel(false);
            CommClient client = DefaultAndriodClient.getCommClient();
            SendEntity entity = new SendEntity();
            entity.setFuncode("2003");
            entity.setVoice(voice);
            byte bytes[] = client.sendAndRecMesssge(JSON.toJSONString(entity).getBytes());
            String str = new String(bytes, "utf-8");
            logger.debug((new StringBuilder()).append("\u53D6\u6D88\u7B7E\u540D\u64CD\u4F5C:").append(str).toString());
        }
        catch(Exception e)
        {
            logger.error(" ", e);
        }
    }

    public static String cancleSignNew(String voice)
    {
        int i = LSCHid.USB_Cancel();
        logger.info((new StringBuilder()).append("\u53D6\u6D88\u6307\u4EE4,\u8FD4\u56DE\u503C : ").append(i).toString());
        FingerProcessImpl.setIsCancel(false);
        CommClient client = DefaultAndriodClient.getCommClient();
        SendEntity entity = new SendEntity();
        entity.setFuncode("2003");
        entity.setVoice(voice);
        byte bytes[] = client.sendAndRecMesssge(JSON.toJSONString(entity).getBytes());
        String str = new String(bytes, "utf-8");
        logger.debug((new StringBuilder()).append("\u53D6\u6D88\u7B7E\u540D\u64CD\u4F5C:").append(str).toString());
        return "0000";
        Exception e;
        e;
        logger.error(" ", e);
        return null;
    }

    public static String pushConfirmPdf(File cofirmPdf)
    {
        logger.debug("\u63A8\u9001\u786E\u8BA4pdf\u6587\u4EF6\u8BA9\u5BA2\u6237\u786E\u8BA4...");
        String retStr;
        if(cofirmPdf == null || !cofirmPdf.exists())
            throw new CommAdapterException("1019");
        PdfProcess process = new PdfProcessHandler();
        SendEntity se = process.doBeforeProcess(cofirmPdf);
        se.setFuncode("1014");
        se.setFormat("pdf");
        CommClient client = DefaultAndriodClient.getCommClient();
        byte rec[] = null;
        String content = JSON.toJSONString(se);
        System.out.println(content);
        try
        {
            rec = client.sendAndRecMesssge(content.getBytes("UTF-8"));
        }
        catch(UnsupportedEncodingException e)
        {
            logger.error("", e);
            throw new CommAdapterException("1007");
        }
        retStr = new String(rec);
        logger.debug((new StringBuilder()).append("\u5BA2\u6237\u786E\u8BA4\u7ED3\u675F,\u8FD4\u56DE\u7ED3\u679C\u4E3A:").append(retStr).toString());
        return retStr;
        CommAdapterException e;
        e;
        e.printStackTrace();
        return null;
    }

    public static Map pushSign(File unsignPdf)
    {
        Map resultMap;
        logger.debug("\u63A8\u9001pdf\u6587\u4EF6\uFF0C\u91C7\u96C6\u5BA2\u6237\u7B7E\u5B57...");
        resultMap = new HashMap();
        if(unsignPdf == null || !unsignPdf.exists())
            break MISSING_BLOCK_LABEL_417;
        RecEntity re;
        PdfProcess process = new PdfProcessHandler();
        SendEntity se = process.doBeforeProcess(unsignPdf);
        se.setFuncode("1001");
        se.setFormat("pdf");
        String content = JSON.toJSONString(se);
        byte rec[] = null;
        System.out.println(content);
        CommClient client = DefaultAndriodClient.getCommClient();
        rec = client.sendAndRecMesssge(content.getBytes("UTF-8"));
        String result = new String(rec);
        re = format.formatJsonRet(result);
        if(!"-821".equals(re.getResultcode()))
            break MISSING_BLOCK_LABEL_163;
        resultMap.put("retCode", "-821");
        resultMap.put("retMess", "\u901A\u4FE1\u88AB\u53D6\u6D88");
        return resultMap;
        if("0000".equals(re.getResultcode()))
        {
            resultMap.put("retCode", "0000");
            String operation = (String)re.getContent().get("operation");
            resultMap.put("retMess", "\u901A\u4FE1\u6210\u529F");
            resultMap.put("operation", operation);
        }
        String signPicture = (String)re.getContent().get("signPng");
        if(StringUtils.isNotBlank(signPicture))
        {
            byte signImg[] = UngzipBaseUtil.uncompress(signPicture);
            resultMap.put("signImg", signImg);
        }
        String signData = (String)re.getContent().get("signData");
        if(StringUtils.isNotEmpty(signData))
            resultMap.put("signData", UngzipBaseUtil.uncompress(signData));
        String signSvg = (String)re.getContent().get("signSvg");
        if(StringUtils.isNotEmpty(signSvg))
            resultMap.put("signSvg", UngzipBaseUtil.uncompress(signSvg));
        logger.debug((new StringBuilder()).append("\u5BA2\u6237\u7B7E\u5B57\u7ED3\u675F,\u8FD4\u56DE\u7ED3\u679C\u4E3A:").append(resultMap).toString());
        return resultMap;
        CommAdapterException e;
        e;
        logger.error("\u4E0E\u67DC\u5916\u8BF7\u901A\u4FE1\u53D1\u751F\u672A\u77E5\u5F02\u5E38");
        e.printStackTrace();
        break MISSING_BLOCK_LABEL_417;
        e;
        logger.error("\u4E0D\u652F\u6301\u7684\u7F16\u7801\u96C6:UTF-8");
        e.printStackTrace();
        return null;
    }

    public static Map pushSignFinger(File unsignPdf, int timeOut)
    {
        logger.debug("\u63A8\u9001pdf\u6587\u4EF6,\u91C7\u53D6\u5BA2\u6237\u6307\u7EB9...");
        if(unsignPdf == null || !unsignPdf.exists())
            break MISSING_BLOCK_LABEL_519;
        logger.debug("pdf\u5B58\u5728\uFF0C\u5F00\u59CB\u8FDB\u884Cpdf\u89E3\u6790...");
        CommClient client;
        String funcode;
        Map fingerMap;
        PdfProcess process = new PdfProcessHandler();
        SendEntity se = process.doBeforeProcess(unsignPdf);
        se.setFuncode("2001");
        se.setVoice("\u8BF7\u786E\u8BA4\u51ED\u8BC1\u4FE1\u606F\u5E76\u6309\u6307\u7EB9\u786E\u8BA4");
        se.setFormat("pdf");
        String content = JSON.toJSONString(se);
        logger.debug("\u5F00\u59CB\u5C06pdf\u63A8\u9001\u5230\u67DC\u5916\u6E05...");
        client = DefaultAndriodClient.getCommClient();
        byte rec[] = client.sendAndRecMesssge(content.getBytes("UTF-8"));
        WZHApi.isCanCancle = Boolean.valueOf(true);
        String recStr = new String(rec);
        logger.debug((new StringBuilder()).append("\u67DC\u5916\u6E05\u8FD4\u56DE\u503C...").append(recStr).toString());
        Object jb = JSON.parse(recStr);
        Map map = (Map)jb;
        funcode = (String)map.get("funcode");
        fingerMap = null;
        if(!"2003".equals("funcode"))
            break MISSING_BLOCK_LABEL_238;
        fingerMap = new HashMap();
        fingerMap.put("retCode", "400");
        fingerMap.put("retMess", "\u53D6\u6D88\u6307\u7EB9\u91C7\u96C6");
        return fingerMap;
        if(!"2001".equals(funcode))
            break MISSING_BLOCK_LABEL_428;
        IFingerProess figerProcess = new FingerProcessImpl();
        FingerProcessImpl.setIsCancel(true);
        try
        {
            logger.debug("\u5F00\u59CB\u8C03\u53D6\u91C7\u96C6\u6307\u7EB9\u6A21\u5757...");
            fingerMap = figerProcess.execute(timeOut);
        }
        catch(Exception e1)
        {
            logger.debug("\u8FDB\u5165\u6307\u7EB9\u91C7\u96C6\u53D1\u751F\u5F02\u5E38catch\u91CC\u9762...");
            fingerMap = new HashMap();
            fingerMap.put("retCode", "400");
            fingerMap.put("retMess", "\u6307\u7EB9\u91C7\u96C6\u53D1\u751F\u5F02\u5E38");
        }
        if(!fingerMap.get("retCode").equals("0000"))
            try
            {
                Thread.currentThread();
                Thread.sleep(800L);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        SendEntity entity = new SendEntity();
        entity.setFuncode("2003");
        entity.setVoice((String)fingerMap.get("retMess"));
        client.sendAndRecMesssge(JSON.toJSONString(entity).getBytes());
        return fingerMap;
        fingerMap = new HashMap();
        fingerMap.put("retCode", "400");
        fingerMap.put("retMess", "\u6307\u7EB9\u91C7\u96C6\u88AB\u53D6\u6D88\u6216\u901A\u4FE1\u4E2D\u65AD");
        return fingerMap;
        CommAdapterException e;
        e;
        logger.error((new StringBuilder()).append("\u4E0E\u67DC\u5916\u8BF7\u901A\u4FE1\u53D1\u751F\u672A\u77E5\u5F02\u5E38:").append(e.toString()).toString());
        break MISSING_BLOCK_LABEL_519;
        e;
        logger.error("\u4E0D\u652F\u6301\u7684\u7F16\u7801\u96C6:UTF-8");
        e.printStackTrace();
        logger.debug("\u6267\u884C\u81F3\u6B64\u5904\uFF0C\u8BF4\u660Epdf\u4E0D\u5B58\u5728\u6216\u4E3A\u7A7A\uFF0C\u6216\u5411\u67DC\u5916\u6E05\u53D1\u9001pdf\u5E76\u672A\u8FD4\u56DE\u6210\u529F\u503C");
        return null;
    }

    public static void byteToImg(byte data[])
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream("d:\\finger1.bmp");
            fos.write(data);
            fos.flush();
            fos.close();
            System.out.println("\u6062\u590D\u6307\u7EB9\u56FE\u7247\u6210\u529F...");
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static Map persistSignInfo(RetEntity ret, String tranId)
    {
        String oper = (String)ret.getCd().get("operation");
        String signDir = WZHApi.Const.getPdfPath();
        if(!(new File(signDir)).exists())
            (new File(signDir)).mkdirs();
        byte signPdf[] = (byte[])(byte[])ret.getCd().get("content");
        String pdfPath = (new StringBuilder()).append(signDir).append(File.separator).append(System.currentTimeMillis()).append("_").append(tranId).append("sign.pdf").toString();
        FileUtil.write(pdfPath, signPdf);
        String imgPath = Pdf2ImgUtil.pdf2Png(pdfPath);
        if(oper != null && "4".equals(oper))
        {
            Map retMap = RetUtil.error("3001", "\u5BA2\u6237\u62D2\u7EDD\u7B7E\u5B57");
            retMap.put("pdf_path", pdfPath);
            retMap.put("img_path", imgPath);
            return retMap;
        } else
        {
            String dataPath = (new StringBuilder()).append(signDir).append(File.separator).append(System.currentTimeMillis()).append("_").append(tranId).append("sign.data").toString();
            byte signData[] = (byte[])(byte[])ret.getCd().get("signData");
            FileUtil.write(dataPath, signData);
            byte signSvg[] = (byte[])(byte[])ret.getCd().get("signSvg");
            String svgPath = (new StringBuilder()).append(signDir).append(File.separator).append(System.currentTimeMillis()).append("_").append(tranId).append("sign.svg").toString();
            FileUtil.write(svgPath, signSvg);
            Map returnMap = RetUtil.success();
            returnMap.put("pdf_path", pdfPath);
            returnMap.put("img_path", imgPath);
            returnMap.put("data_path", dataPath);
            returnMap.put("svg_path", svgPath);
            return returnMap;
        }
    }

    /**
     * @deprecated Method goSignFiner is deprecated
     */

    static Map goSignFiner(String content, String tranId)
    {
        RetEntity re;
        String oper;
        Map retMap;
        FileUtil.write((new StringBuilder()).append(sign_path).append(tranId).toString(), content);
        logger.debug((new StringBuilder()).append("receive teller info:==").append(content).append("||tranId:").append(tranId).toString());
        if(StringUtils.isBlank(content) || StringUtils.isBlank(tranId))
            throw new CommAdapterException("1019");
        XmlUnFormatHandler handler = XmlFormatHandlerFactory.getXmlFormatInstance(tranId);
        if(null == handler)
            handler = XmlFormatHandlerFactory.getDefaultFormatInstance();
        Map paramMap = handler.unformatPrintHandler(content, false, tranId);
        paramMap.put("tranId", tranId);
        paramMap.put("jiaoyima", tranId);
        paramMap.put("funcode", "2001");
        String jsonContent = JSON.toJSONString(paramMap);
        logger.info((new StringBuilder()).append("jsonContent: ").append(jsonContent).toString());
        re = DefaultCommClient.sendMessage(jsonContent);
        if(!re.isOk())
            throw new CommAdapterException(re.getEc(), re.getEm());
        oper = (String)re.getCd().get("operation");
        if(oper == null || !"0".equals(oper))
            break MISSING_BLOCK_LABEL_251;
        retMap = RetUtil.error("3002", "\u5BA2\u6237\u957F\u65F6\u95F4\u672A\u64CD\u4F5C");
        return retMap;
        String signDir;
        String pdfPath;
        String imgPath;
        Map retMap;
        signDir = WZHApi.Const.getPdfPath();
        if(!(new File(signDir)).exists())
            (new File(signDir)).mkdirs();
        byte signPdf[] = (byte[])(byte[])re.getCd().get("content");
        pdfPath = (new StringBuilder()).append(signDir).append(File.separator).append(System.currentTimeMillis()).append("_").append(tranId).append("sign.pdf").toString();
        FileUtil.write(pdfPath, signPdf);
        imgPath = Pdf2ImgUtil.pdf2Png(pdfPath);
        if(oper == null || !"4".equals(oper))
            break MISSING_BLOCK_LABEL_412;
        retMap = RetUtil.error("3001", "\u5BA2\u6237\u62D2\u7EDD\u7B7E\u5B57");
        retMap.put("pdf_path", pdfPath);
        retMap.put("img_path", imgPath);
        return retMap;
        Map returnMap;
        String dataPath = (new StringBuilder()).append(signDir).append(File.separator).append(System.currentTimeMillis()).append("_").append(tranId).append("sign.data").toString();
        byte signData[] = (byte[])(byte[])re.getCd().get("signData");
        FileUtil.write(dataPath, signData);
        byte signSvg[] = (byte[])(byte[])re.getCd().get("signSvg");
        String svgPath = (new StringBuilder()).append(signDir).append(File.separator).append(System.currentTimeMillis()).append("_").append(tranId).append("sign.svg").toString();
        FileUtil.write(svgPath, signSvg);
        returnMap = RetUtil.success();
        returnMap.put("pdf_path", pdfPath);
        returnMap.put("img_path", imgPath);
        returnMap.put("data_path", dataPath);
        returnMap.put("svg_path", svgPath);
        return returnMap;
        Exception e;
        e;
        logger.error(" ", e);
        return RetUtil.error(e);
    }

    static Map uploadToWZH(String contentPath, byte signData[], byte signSvg[], byte fingerImg[], java.util.List attachFileList, String jsonData)
    {
        FileInputStream fis = null;
        Map map1;
        logger.debug((new StringBuilder()).append("receive teller info:==").append(contentPath).append("||jsonData:").append(jsonData).toString());
        if(contentPath == null || jsonData == null)
            throw new CommAdapterException("1019");
        File content = new File(contentPath);
        byte content_buff[] = null;
        if(content.exists())
        {
            fis = new FileInputStream(content);
            if(fis != null)
            {
                int length = fis.available();
                content_buff = new byte[length];
                fis.read(content_buff);
            }
        } else
        {
            throw new CommAdapterException("1023");
        }
        Map paramMap = format.formatJsonArg(jsonData);
        String path = HttpUploadFileTemp.uploadFile(content_buff, signData, signSvg, fingerImg, attachFileList, jsonData);
        Map returnMap = RetUtil.success();
        returnMap.put("path", path);
        map1 = returnMap;
        if(fis != null)
            try
            {
                fis.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        return map1;
        Exception e;
        e;
        Map map;
        logger.error(" ", e);
        map = RetUtil.error(e);
        if(fis != null)
            try
            {
                fis.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        return map;
        Exception exception;
        exception;
        if(fis != null)
            try
            {
                fis.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        throw exception;
    }

    static Map uploadToWZHCart(String contentPath, byte signData[], byte signSvg[], byte fingerImg[], java.util.List attachFileList, String jsonData)
    {
        FileInputStream fis = null;
        Map map1;
        logger.debug((new StringBuilder()).append("receive teller info:==").append(contentPath).append("||jsonData:").append(jsonData).toString());
        if(contentPath == null || jsonData == null)
            throw new CommAdapterException("1019");
        File content = new File(contentPath);
        byte content_buff[] = null;
        if(content.exists())
        {
            fis = new FileInputStream(content);
            if(fis != null)
            {
                int length = fis.available();
                content_buff = new byte[length];
                fis.read(content_buff);
            }
        } else
        {
            throw new CommAdapterException("1023");
        }
        Map paramMap = format.formatJsonArg(jsonData);
        String path = HttpUploadFileTemp.uploadFileCart(content_buff, signData, signSvg, fingerImg, attachFileList, jsonData);
        Map returnMap = RetUtil.success();
        returnMap.put("path", path);
        map1 = returnMap;
        if(fis != null)
            try
            {
                fis.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        return map1;
        Exception e;
        e;
        Map map;
        logger.error(" ", e);
        map = RetUtil.error(e);
        if(fis != null)
            try
            {
                fis.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        return map;
        Exception exception;
        exception;
        if(fis != null)
            try
            {
                fis.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        throw exception;
    }

    public static long getVersion(String funcode)
    {
        String ver;
        CommClient client = DefaultAndriodClient.getCommClient();
        SendEntity entity = new SendEntity();
        entity.setFuncode(funcode);
        byte rec[] = client.sendAndRecMesssge(JSON.toJSONString(entity).getBytes());
        String retStr = new String(rec);
        RecEntity re = format.formatJsonRet(retStr);
        if(!"0000".equals(re.getResultcode()))
            break MISSING_BLOCK_LABEL_89;
        ver = (String)re.getContent().get("content");
        return Long.parseLong(ver);
        return -1L;
        CommAdapterException e;
        e;
        e.printStackTrace();
        return -1L;
    }

    public static RecEntity evaluate(TellerInfo info)
    {
        RecEntity re = null;
        try
        {
            CommClient client = DefaultAndriodClient.getCommClient();
            SendEntity entity = new SendEntity();
            entity.setFuncode("1003");
            Map map = new HashMap();
            map.put("headImg", info.getImgData() == null ? "" : ((Object) (info.getImgData())));
            map.put("type", info.getImgType() == null ? "" : ((Object) (info.getImgType())));
            map.put("motto", info.getMotto());
            map.put("workNumber", info.getWorkNumber());
            map.put("name", info.getName());
            entity.setContent(map);
            byte rec[] = client.sendAndRecMesssge(JSON.toJSONString(entity).getBytes());
            String retStr = new String(rec);
            re = format.formatJsonRet(retStr);
            logger.debug((new StringBuilder()).append("\u5BA2\u6237\u8BC4\u4EF7\u7ED3\u679C:").append(re).toString());
        }
        catch(CommAdapterException e)
        {
            e.printStackTrace();
        }
        return re;
    }

    private static final Logger logger = LoggerFactory.getLogger(com/ebank/commAdapter/api/LfCommClient2);
    private static final String confirm_path;
    private static final String sign_path;
    private static JsonFormat format = new DefaultJsonFormat();
    private static ConvertProccessHandler pr = new ConvertProccessHandler();
    static volatile boolean isPreview = false;
    static volatile boolean isBtnEnable = false;

    static 
    {
        confirm_path = (new StringBuilder()).append(Configuration.ROOT_PATH).append("/log/confirm/").toString();
        sign_path = (new StringBuilder()).append(Configuration.ROOT_PATH).append("/log/sign/").toString();
    }

}
