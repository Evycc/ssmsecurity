package com.evy.jing.util;

import com.evy.jing.model.SeUser;
import com.evy.jing.redis.JedisCache;
import com.evy.jing.service.SeUserService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 发送邮箱类
 */
@Component
public class SendEmail {
    private static MimeMessageHelper mimeMessageHelper;
    private static JavaMailSender mailSender;
    private static SeUserService seUserService;
    private static JedisCache cache;

    @PostConstruct
    private void init(){
        mailSender = FactoryStaticResource.getMailSender();
        mimeMessageHelper = FactoryStaticResource.getMimeMessageHelper();
        cache = FactoryStaticResource.getCache();
        seUserService = FactoryStaticResource.getSeUserService();
    }

    /**
     * 60s后清除重设密码连接缓存
     */
    private static final int TIMEOUT = 600;
    private static final String PREFIX = "http://39.107.95.15:8080";
    private static final String RESET_PASSWORD_URL = PREFIX + "/resetPassword";

    /**
     * 默认发送密码更改邮件
     * @param email
     */
    public static void sendEditPassword(String email){

        List<SeUser> userList = seUserService.findByEmail(email);
        if (userList == null) {
            LoggerUtils.errorStr(SendEmail.class,
                    "sendEditPassword Error! email:%s 的对象为空。", email);
            return;
        }
        String username = userList.get(0).getSsoId();
        //连接由UUID与电子邮箱序列化字符串构成
        StringBuilder btnUrl = new StringBuilder();
        btnUrl.append(RESET_PASSWORD_URL);
        UUID uuid = UUID.randomUUID();
        UUID emailId = UUID.nameUUIDFromBytes(SerializeUtils.ofSerialize(email));
        cache.setTimeOut(TIMEOUT);
        cache.put(uuid.toString(), emailId.toString());
        cache.put(emailId.toString(), email);
        LoggerUtils.info(SendEmail.class, "uuid:" + uuid);
        LoggerUtils.info(SendEmail.class, "emailId:" + emailId);

        //test
        System.out.println("uuid = " + uuid);
        System.out.println("emailId = " + emailId);

        btnUrl.append("?uuid=" + uuid);
        btnUrl.append("&email_guid=" + emailId);
        btnUrl.append("&email_link=btn_link");

        String text = "<div style='text-align: center;'>" +
                "<div style='width: 400px;" +
                "margin: 0 auto;" +
                "text-align: initial;" +
                "border-bottom: 1px solid #bbbbbb;'>" +
                "<h1 style='color: #2dbe60;font-weight: 100;font-size: 30px;'>修改密码</h1>" +
                "<p style='margin: 0 0 10px;" +
                "color: #484848;" +
                "font-family: helvetica, arial, sans-serif;" +
                "font-size: 18px;" +
                "line-height: 26px;" +
                "font-weight: 100;" +
                "padding: 0 30px 16px 0;'>" +
                "我们收到你(" +
                username +
                ")发来的密码修改申请。 " +
                "</p>" +
                "<p style='margin: 0 0 10px;" +
                "color: #484848;" +
                "font-family: helvetica, arial, sans-serif;" +
                "font-size: 18px;" +
                "line-height: 26px;" +
                "font-weight: 100;" +
                "padding: 0 30px 16px 0;'>" +
                "如果你没有提出过修改密码申请，请忽略此邮件，你的密码不会被修改。下方链接在 25 小时内有效。" +
                "</p>" +
                "<p>" +
                "<a href='" +
                btnUrl.toString() +
                "' style='" +
                "background-color: #2dbe60;" +
                "border: 1px solid #2dbe60;" +
                "border-radius: 4px;" +
                "color: #ffffff;" +
                "display: inline-block;" +
                "font-family: Helvetica,Arial,sans-serif;" +
                "font-size: 14px;" +
                "font-weight: bold;" +
                "line-height: 35px;" +
                "text-align: center;" +
                "text-decoration: none;" +
                "-webkit-text-size-adjust: none;" +
                "mso-hide: all;" +
                "padding: 0 25px 0 25px;" +
                "letter-spacing: .5px;" +
                "min-width: 150px;'>" +
                "重设密码" +
                "</a>" +
                "</p>" +
                "</div>" +
                "</div>" +
                "<div style='width: 400px;" +
                "margin: 0 auto;" +
                "padding-top: 20px;" +
                "font-size: 16px;" +
                "text-align: center;" +
                "color: #535353;'>" +
                "2017 " +
                "<a href='' style = 'text-decoration: none; color: #2dbe60;'>Evy</a>" +
                "</div>";
        try {
            mimeMessageHelper.setSubject("密码更改请求");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setText(text, true);
            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置密码成功时发送邮件
     * @param map   map k:uuid  k:email_guid  k:password  k:browser  k:os  k:user_ip k:email
     */
    public static void sendChangePasswordSuccess(Map map){
        String email = (String) map.get("email");
        String browser = (String) map.get("browser");
        String os = (String) map.get("os");
        String ip = (String) map.get("user_ip");

        List<SeUser> userList = seUserService.findByEmail(email);
        if (userList == null) {
            LoggerUtils.errorStr(SendEmail.class,
                    "sendEditPassword Error! email:%s 的对象为空。", email);
            return;
        }
        String username = userList.get(0).getSsoId();
        //连接由UUID与电子邮箱序列化字符串构成
        StringBuilder btnUrl = new StringBuilder();
        btnUrl.append(RESET_PASSWORD_URL);
        UUID uuid = UUID.randomUUID();
        UUID emailId = UUID.nameUUIDFromBytes(SerializeUtils.ofSerialize(email));
        cache.setTimeOut(TIMEOUT);
        cache.put(uuid.toString(), emailId.toString());
        cache.put(emailId.toString(), email);
        LoggerUtils.info(SendEmail.class, "uuid:" + uuid);
        LoggerUtils.info(SendEmail.class, "emailId:" + emailId);

        //test
        System.out.println("uuid = " + uuid);
        System.out.println("emailId = " + emailId);

        btnUrl.append("?uuid=" + uuid);
        btnUrl.append("&email_guid=" + emailId);
        btnUrl.append("&email_link=btn_link");

        String text = "<div style='text-align: center;'>" +
                "<div style='width: 400px;" +
                "margin: 0 auto;" +
                "text-align: initial;" +
                "padding-bottom: 20px;" +
                "border-bottom: 1px solid #bbbbbb;'>" +
                "<h1 style='color: #2dbe60;font-weight: 100;font-size: 30px;'>密码修改成功</h1>" +
                "<p style='margin: 0 0 10px;" +
                "color: #484848;" +
                "font-family: helvetica, arial, sans-serif;" +
                "font-size: 18px;" +
                "line-height: 26px;" +
                "font-weight: 100;" +
                "padding: 0 30px 16px 0;'>" +
                "你的帐户(" +
                username +
                ")最近修改过密码，修改是在以下电脑或浏览器进行的：" +
                "</p>" +
                "<p style='margin: 0 0 10px;" +
                "color: #484848;" +
                "font-family: helvetica, arial, sans-serif;" +
                "font-size: 18px;" +
                "line-height: 26px;" +
                "font-weight: 100;" +
                "padding: 0 30px 16px 0;'>" +
                "操作系统/浏览器：" +
                os + "/" + browser +
                "</p>" +
                "<p style='margin: 0 0 10px;" +
                "color: #484848;" +
                "font-family: helvetica, arial, sans-serif;" +
                "font-size: 18px;" +
                "line-height: 26px;" +
                "font-weight: 100;" +
                "padding: 0 30px 16px 0;'>" +
                "IP地址：" +
                ip +
                "</p>" +
                "<p style='margin: 0 0 10px;" +
                "color: #484848;" +
                "font-family: helvetica, arial, sans-serif;" +
                "font-size: 18px;" +
                "line-height: 26px;" +
                "font-weight: 100;" +
                "padding: 0 30px 16px 0;'>" +
                "如果密码是在你不知情的情况下进行了修改，请点击下面的链接重新修改。该链接自邮件发出25小时内有效。" +
                "</p>" +
                "<p>" +
                "<a href='" +
                btnUrl.toString() +
                "' style='" +
                "background-color: #2dbe60;" +
                "border: 1px solid #2dbe60;" +
                "border-radius: 4px;" +
                "color: #ffffff;" +
                "display: inline-block;" +
                "font-family: Helvetica,Arial,sans-serif;" +
                "font-size: 14px;" +
                "font-weight: bold;" +
                "line-height: 35px;" +
                "text-align: center;" +
                "text-decoration: none;" +
                "-webkit-text-size-adjust: none;" +
                "mso-hide: all;" +
                "padding: 0 25px 0 25px;" +
                "letter-spacing: .5px;" +
                "min-width: 150px;'>" +
                "重设密码" +
                "</a>" +
                "</p>" +
                "</div>" +
                "<div style='width: 400px;" +
                "margin: 0 auto;" +
                "padding-top: 20px;" +
                "font-size: 16px;" +
                "text-align: center;" +
                "color: #535353;'>" +
                "2017 " +
                "<a href='' style = 'text-decoration: none; color: #2dbe60;'>Evy</a>" +
                "</div>";
        try {
            mimeMessageHelper.setSubject("密码更改确认");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setText(text, true);
            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
