package com.management.admin.utils;

import com.management.admin.entity.Constant;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class WechatUtil {

    private static final Logger logger = LoggerFactory.getLogger(WechatUtil.class);

    private static final String APP_ID = "wx54febf9bad7035bd";

    private static final String MERCHANT_ID = "1525849651";

    private static final String MCHKEY = "kTeYaS2fU4T7ZzGUwOqargLzvRvN3xdE";

    private static WechatXmlUtil wechatXmlUtil;

    static {
        wechatXmlUtil = new WechatUtil().getWechatXmlUtil();
    }

    public WechatXmlUtil getWechatXmlUtil() {
        return this.new WechatXmlUtil();
    }


    public static Map unifiedOrder(Double price, String orderId, String productName, String notify, HttpServletRequest request) {
        Map resultMap = new HashMap();
        int x = (int) (Float.valueOf(price.floatValue()) * 100);
        String total_fee = x + "";
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        packageParams.put("appid", APP_ID);  // 微信开发平台应用ID 下面参数都不完整  需要写入自己的参数
        packageParams.put("mch_id", MERCHANT_ID);
        packageParams.put("nonce_str", getCurrTime() + buildRandom(4) + "");
        packageParams.put("body", productName);//商品标题
        packageParams.put("out_trade_no", orderId);// 商户订单号
        packageParams.put("total_fee", total_fee);// 总金额
        String addr = getIpAddr(request);
        packageParams.put("spbill_create_ip", addr);// 发起人IP地址
        packageParams.put("notify_url", notify);// 回调地址
        packageParams.put("trade_type", "APP");// 交易类型
        packageParams.put("time_start", getTimeStamp());
        String sign = createSign("UTF-8", packageParams, MCHKEY); //应用对应的密钥
        packageParams.put("sign", sign);// 签名
        String requestXML = wechatXmlUtil.getRequestXml(packageParams);
        try {
            String post = HttpUtils.post("https://api.mch.weixin.qq.com/pay/unifiedorder", requestXML);
            Map map = wechatXmlUtil.doXMLParse(post);
            String returnCode = (String) map.get("return_code");
            String returnMsg = (String) map.get("return_msg");
            logger.info("请求结果码为：" + returnCode);
            logger.info("请求结果信息：" + returnMsg);
            if ("SUCCESS".equals(returnCode)) {
                String resultCode = (String) map.get("result_code");
                String prepay_id = (String) map.get("prepay_id");
                String noncestr = (String) map.get("nonce_str");
                if ("SUCCESS".equals(resultCode)) {
                    System.out.println("获取prepay_id成功" + prepay_id);// 必须获取到这个prepay_id才算微信认可了你的第一次签名

                    // 封装了开始调起支付接口数据
                    SortedMap<Object, Object> packageParam = new TreeMap<Object, Object>();
                    packageParam.put("appid", APP_ID); //微信开发平台应用ID 下面参数都不完整  需要写入自己的参数
                    packageParam.put("partnerid", MERCHANT_ID);
                    packageParam.put("prepayid", prepay_id);// 商品描述
                    packageParam.put("package", "Sign=WXPay");// 商户订单号
                    packageParam.put("noncestr", noncestr);
                    packageParam.put("timestamp", getTimeStamp());
                    String sign1 = createSign("UTF-8", packageParam, MCHKEY);// 这里是二次签名    //应用对应的密钥
                    // 处理交易订单信息 // 前台要拿到去调起微信支付，如果这个错了的话会在前台报签名错误
                    Map<String, Object> jsonMap = new TreeMap<String, Object>();
                    jsonMap.put("appid", "wx54febf9bad7035bd");//微信开发平台应用ID 下面参数都不完整  需要写入自己的参数
                    jsonMap.put("noncestr", noncestr);
                    jsonMap.put("package", "Sign=WXPay");
                    jsonMap.put("partnerid", MERCHANT_ID);
                    jsonMap.put("prepayid", prepay_id);
                    jsonMap.put("timestamp", getTimeStamp());
                    jsonMap.put("sign", sign1);
                    resultMap = jsonMap;
                    logger.info(jsonMap.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 获取微信签名随机字符串
     * @return
     */
    public static String getNonceStr(){
        // 随机字符串
       return getCurrTime()+buildRandom(4);
    }

    public static Map doXMLParse(String resp) throws Exception{
        return wechatXmlUtil.doXMLParse(resp);
    }

    /**
     * 获取支付价格（微信单位分）
     * @param price
     * @return
     */
    public static Integer getTotalFee(Double price){
        return (int) (Float.valueOf(price.floatValue()) * 100);
    }

    /**
     * 组装微信请求参数成xml格式的字符串
     * @param map
     * @return
     */
    public static String getRequestXML(SortedMap<Object,Object> map){
        return wechatXmlUtil.getRequestXml(map);
    }

    public static Map notify(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> map = null;
        try {
            System.out.println("微信支付回调");
            PrintWriter writer = response.getWriter();
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String result = new String(outSteam.toByteArray(), "utf-8");
            System.out.println("微信支付通知结果：" + result);


            /**
             * 解析微信通知返回的信息
             */
            map = wechatXmlUtil.doXMLParse(result);

            System.out.println("=========:" + result);
            // 若支付成功，则告知微信服务器收到通知
            if (map.get("return_code").

                    equals("SUCCESS")) {
                if (map.get("result_code").equals("SUCCESS")) {
                    Long orderId = Long.parseLong(map.get("out_trade_no"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 获取当前时间 yyyyMMddHHmmss
     *
     * @return String
     */
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }

    public static void main(String[] args) {
        System.out.println(buildRandom(4));
        System.out.println(getCurrTime());
    }
    /**
     * 取出一个指定长度大小的随机正整数.
     *
     * @param length
     * @return int
     * @Author japhet_jiu
     * @Date 2017年7月31日
     */
    public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 获取IP地址
     *
     * @param request
     * @return String
     * @Author japhet_jiu
     * @Date 2017年7月31日
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip))
            return ip;
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1)
                return ip.substring(0, index);
            else
                return ip;
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("Proxy-Client-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("HTTP_CLIENT_IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();
        if (ip == null || ip != null && ip.indexOf("0:0:0:0:0:0:0") != -1) {
            return "127.0.0.1";
        }
        return ip;
    }

    /**
     * @param characterEncoding 编码格式
     * @param
     * @return
     * @author japhet_jiu
     * @Description：sign签名
     */
    @SuppressWarnings({"rawtypes"})
    public static String createSign(String characterEncoding, SortedMap<Object, Object> packageParams, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + API_KEY);
        String sign = MD5Util.encrypt32(sb.toString());
        return sign;
    }

    public static String createSign(SortedMap<Object, Object> packageParams, String API_KEY) {
        return createSign("UTF-8",packageParams,API_KEY);
    }

    /**
     * MD5Encode
     *
     * @param origin
     * @param charsetname
     * @return
     */
    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }

    /**
     * MD5加密
     *
     * @param b
     * @return
     */
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    /**
     * byteToHexString
     *
     * @param b
     * @return
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * hexDigits
     */
    private static final String hexDigits[] = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"
    };

    class WechatXmlUtil {
        /**
         * 获取子结点的xml
         *
         * @param children
         * @return String
         */
        @SuppressWarnings({"rawtypes"})
        public String getChildrenText(List children) {
            StringBuffer sb = new StringBuffer();
            if (!children.isEmpty()) {
                Iterator it = children.iterator();
                while (it.hasNext()) {
                    Element e = (Element) it.next();
                    String name = e.getName();
                    String value = e.getTextNormalize();
                    List list = e.getChildren();
                    sb.append("<" + name + ">");
                    if (!list.isEmpty()) {
                        sb.append(getChildrenText(list));
                    }
                    sb.append(value);
                    sb.append("</" + name + ">");
                }
            }
            return sb.toString();
        }

        /**
         * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
         *
         * @param strxml
         * @return
         * @throws JDOMException
         * @throws IOException
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        public Map doXMLParse(String strxml) throws JDOMException, IOException {
            strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

            if (null == strxml || "".equals(strxml)) {
                return null;
            }

            Map m = new HashMap();

            InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            List list = root.getChildren();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String k = e.getName();
                String v = "";
                List children = e.getChildren();
                if (children.isEmpty()) {
                    v = e.getTextNormalize();
                } else {
                    v = getChildrenText(children);
                }
                m.put(k, v);
            }
            // 关闭流
            in.close();
            return m;
        }

        /**
         * 将请求参数转换为xml格式的string
         *
         * @param parameters
         * @return String
         * @Author japhet_jiu
         * @Date
         */
        @SuppressWarnings({"rawtypes"})
        public String getRequestXml(SortedMap<Object, Object> parameters) {
            StringBuffer sb = new StringBuffer();
            sb.append("<xml>");
            Set es = parameters.entrySet();
            Iterator it = es.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String k = (String) entry.getKey();
                String v = (String) entry.getValue();
                //  || "sign".equalsIgnoreCase(k)
                //|| "body".equalsIgnoreCase(k)
                if ("attach".equalsIgnoreCase(k) ) {
                    sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
                } else {
                    sb.append("<" + k + ">" + v + "</" + k + ">");
                }
            }
            sb.append("</xml>");
            return sb.toString();
        }
    }
}
