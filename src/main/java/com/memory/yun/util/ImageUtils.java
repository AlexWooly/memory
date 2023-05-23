package com.memory.yun.util;



import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Author NJUPT wly
 * @Date 2021/8/7 2:23 上午
 * @Version 1.0
 */
@Slf4j
@Component
public class ImageUtils {
    /**
     * 缩放比例系数
     */
    private static double SCALING = 0.56;
    /**
     * 符合base64的宽
     */
    private static int MAX_WIDTH = 560;
    /**
     * 最大高
     */
    private static int MAX_HEIGHT = 1000;

    /**
     * @Description 根据图片公网地址转BufferedImage
     * @param url 图片公网地址
     * @return java.awt.image.BufferedImage
     **/
    public  BufferedImage imgUrlConvertBufferedImage(String url) throws Exception {
        URL urls = new URL(url);
        Image image = Toolkit.getDefaultToolkit().getImage(urls);
        BufferedImage bufferedImage = toBufferedImage(image);
        return bufferedImage;
    }
    /**
     * @Description 根据BufferedImage处理图片并返回byte[]
     * @param bufferedImage
     * @return byte[]
     **/
    public  byte[] zoomImageByte(BufferedImage bufferedImage) throws Exception {
        ByteArrayOutputStream outputStreamZoom = new ByteArrayOutputStream();
        ByteArrayOutputStream outputStreamSource = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStreamSource);
        BufferedImage bufferedImageZoom = zoomImage(outputStreamSource.toByteArray());
        //写入缩减后的图片
        ImageIO.write(bufferedImageZoom, "jpg", outputStreamZoom);
        return outputStreamZoom.toByteArray();
    }

    /**
     * @Description 根据byte[]处理图片并返回byte[]
     * @param src
     * @return byte[]
     **/
    public  byte[] zoomImageByte(byte[] src) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage bufferedImage = zoomImage(src);
        //写入缩减后的图片
        ImageIO.write(bufferedImage, "jpg", outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 图片缩放 仅适用于微信内容图片安全检测使用
     *
     * @param src 为源文件byte
     */
    public  BufferedImage zoomImage(byte[] src) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(src);
        double wr = 0, hr = 0;
        BufferedImage bufferedImage = null;
        //读取图片
        BufferedImage bufImg = ImageIO.read(in);
        int height = bufImg.getHeight();
        int width = bufImg.getWidth();
        int cHeight = height;
        int cWidth = width;
        double Scaling = width / height;
        if (Scaling < SCALING) {
            if (height > MAX_HEIGHT) {
                cHeight = MAX_HEIGHT;
                cWidth = (width * MAX_HEIGHT) / height;
            }
            //以宽为缩放比例
        } else {
            if (width > MAX_WIDTH) {
                cWidth = MAX_WIDTH;
                cHeight = (height * MAX_WIDTH) / width;
            }
        }
        //获取缩放后的宽高
        log.info("宽{},高{}", cWidth, cHeight);
        //设置缩放目标图片模板
        Image Itemp = bufImg.getScaledInstance(width, cHeight, BufferedImage.SCALE_SMOOTH);
        //获取缩放比例
        wr = cWidth * 1.0 / width;
        hr = cHeight * 1.0 / height;
        log.info("宽比例{},高比例{}", wr, hr);
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bufImg, null);
        try {
            //写入缩减后的图片
            ImageIO.write((BufferedImage) Itemp, "jpg", outputStream);
            ByteArrayInputStream inNew = new ByteArrayInputStream(outputStream.toByteArray());
            bufferedImage = ImageIO.read(inNew);
        } catch (Exception ex) {
            log.info("缩放图片异常{}", ex.getMessage());
        } finally {
            if (null != outputStream) {
                outputStream.close();
            }
            if (null != in) {
                in.close();
            }
        }
        return bufferedImage;
    }

    /**
     * @Description Image转BufferedImage
     * @param image 通过url获取的image对象
     * @return java.awt.image.BufferedImage
     **/
    public  BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null),
                    image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    /**
     * 根据图片地址返回图片base64
     * @param picUrl 图片地址
     * @return
     */
    public  String downloadPicUrl(String picUrl){
        URL url = null;
        String suffix = picUrl.substring(picUrl.lastIndexOf(".")+1);
        try {
            url = new URL(picUrl);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Image image = Toolkit.getDefaultToolkit().getImage(url);
            BufferedImage bufferedImage = toBufferedImage(image);
            ImageIO.write(bufferedImage,suffix,byteArrayOutputStream);
            String base64 = Base64Util.encode(Arrays.toString(byteArrayOutputStream.toByteArray()));
            return base64;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传二进制文件
     * @param graphurl 接口地址
     * @param file 图片文件
     * @return
     */
    public  String uploadFile(String graphurl, MultipartFile file) {
        String line = null;//接口返回的结果
        try {
            // 换行符
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            // 定义数据分隔线
            String BOUNDARY = "========7d4a6d158c9";
            // 服务器的域名
            URL url = new URL(graphurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置为POST情
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
            OutputStream out = new DataOutputStream(conn.getOutputStream());

            // 上传文件
            StringBuilder sb = new StringBuilder();
            sb.append(boundaryPrefix);
            sb.append(BOUNDARY);
            sb.append(newLine);
            // 文件参数,photo参数名可以随意修改
            sb.append("Content-Disposition: form-data;name=\"image\";filename=\""
                    + "https://api.weixin.qq.com" + "\"" + newLine);
            sb.append("Content-Type:application/octet-stream");
            // 参数头设置完以后需要两个换行，然后才是参数内容
            sb.append(newLine);
            sb.append(newLine);

            // 将参数头的数据写入到输出流中
            out.write(sb.toString().getBytes());

            // 读取文件数据
            out.write(file.getBytes());
            // 最后添加换行
            out.write(newLine.getBytes());

            // 定义最后数据分隔线，即--加上BOUNDARY再加上--。
            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY
                    + boundaryPrefix + newLine).getBytes();
            // 写上结尾标识
            out.write(end_data);
            out.flush();
            out.close();
            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                return line;
            }
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
        }
        return line;
    }

    /**
     * 上传二进制文件
     * @param apiurl 接口地址
     * @param file 图片文件
     * @return
     */
    public  String uploadFile(String apiurl, byte[] file) {
        //接口返回的结果
        String line = null;
        try {
            // 换行符
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            // 定义数据分隔线
            String BOUNDARY = "========7d4a6d158c9";
            // 服务器的域名
            URL url = new URL(apiurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置为POST情
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.15(0x17000f31) NetType/WIFI Language/zh_CN");
            OutputStream out = new DataOutputStream(conn.getOutputStream());

            // 上传文件
            StringBuilder sb = new StringBuilder();
            sb.append(boundaryPrefix);
            sb.append(BOUNDARY);
            sb.append(newLine);
            // 文件参数,photo参数名可以随意修改
            sb.append("Content-Disposition: form-data;name=\"image\";filename=\""
                    + "https://api.weixin.qq.com" + "\"" + newLine);
            sb.append("Content-Type:application/octet-stream");
            // 参数头设置完以后需要两个换行，然后才是参数内容
            sb.append(newLine);
            sb.append(newLine);

            // 将参数头的数据写入到输出流中
            out.write(sb.toString().getBytes());

            // 读取文件数据
            out.write(file);
            // 最后添加换行
            out.write(newLine.getBytes());

            // 定义最后数据分隔线，即--加上BOUNDARY再加上--。
            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY
                    + boundaryPrefix + newLine).getBytes();
            // 写上结尾标识
            out.write(end_data);
            out.flush();
            out.close();
            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                return line;
            }
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
        }
        return line;
    }

    public boolean checkImg(MultipartFile photo) throws IOException {
        //获取文件全名
        String photoName = photo.getOriginalFilename();
        //首先判断是不是空的文件
        if (!photo.isEmpty()) {
            //对文文件的全名进行截取然后在后缀名进行删选。
            int begin = Objects.requireNonNull(photo.getOriginalFilename()).lastIndexOf(".");
            int last = photo.getOriginalFilename().length();
            //获得文件后缀名
            String suffix = photo.getOriginalFilename().substring(begin + 1, last);
            //这里是正确的图片格式
            String a = suffix.toUpperCase();
            return ("GIF").equals(a) || ("JPG").equals(a) || ("PNG").equals(a) || ("JPEG").equals(a);
        }
        return false;
    }

    public boolean checkImg(MultipartFile file,int mb){
        long size = file.getSize();
        return size < (long) mb;
    }



}
