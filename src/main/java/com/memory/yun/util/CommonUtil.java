package com.memory.yun.util;

import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author NJUPT wly
 * @Date 2021/9/13 3:38 下午
 * @Version 1.0
 */
@Slf4j
public class CommonUtil {

    /**
     * 获取ip
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
        }
        return ipAddress;
    }



    public static String MD5(String data)  {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }

            return sb.toString().toUpperCase();
        } catch (Exception exception) {
        }
        return null;

    }

    /**
     * 获取随机数
     * @param length
     * @return
     */
    public static String getRandomCode(int length){
        String source = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int j=0; j<length; j++){
            sb.append(source.charAt(random.nextInt(9)));
        }
        return sb.toString();
    }

    public static long getCurrentTimestamp(){
        return System.currentTimeMillis();
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-","").substring(0,32);
    }

    /**
     * 生成指定长度随机字母和数字
     *
     * @param length
     * @return
     */
    private static final String ALL_CHAR_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static String getStringNumRandom(int length) {
        //生成随机数字和字母,
        Random random = new Random();
        StringBuilder saltString = new StringBuilder(length);
        for (int i = 1; i <= length; ++i) {
            saltString.append(ALL_CHAR_NUM.charAt(random.nextInt(ALL_CHAR_NUM.length())));
        }
        return saltString.toString();
    }

    /**
     * 响应数据给前端
     * @param response
     * @param obj
     */
    public static void sendJsonMessage(HttpServletResponse response, Object obj){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        try(PrintWriter printWriter = response.getWriter();) {
            printWriter.print(objectMapper.writeValueAsString(obj));

            response.flushBuffer();
        } catch (IOException e) {
            log.warn("响应给q前端数据异常:{}",e);
        }
    }

    /**
     * 获取全部请求头
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestHeader(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> map = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            //根据名称获取请求头的值
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    /**
     * 默认日期格式
     */
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd";

    /**
     * 默认日期格式
     */
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER  = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();


    /**
     *  Date 转 字符串，默认日期格式
     * @param time
     * @return
     */
    public static String format(Date time){
        String timeStr = DEFAULT_DATE_FORMATTER.format(time.toInstant().atZone(DEFAULT_ZONE_ID));
        return timeStr;
    }

    public static String formatCh(Date time){
        String format = format(time);
        String[] split = format.split("-");
        String year = split[0];
        String month = split[1];
        if (month.startsWith("0")){
            month = month.substring(1);
        }
        return year+"年"+month+"月";
    }

    public static void main(String[] args) {
        System.out.println(formatCh(new Date()));
    }


}
