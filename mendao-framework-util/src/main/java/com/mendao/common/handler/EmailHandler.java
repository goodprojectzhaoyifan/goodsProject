package com.mendao.common.handler;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import com.mendao.util.PropertiesUtil;

public class EmailHandler {

	private Properties props;
	
	protected EmailHandler(String smtpHost, String sendFrom, String sendPwd){
		
		props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.user", sendFrom);
		props.put("mail.password", sendPwd);
		 
	}
	
	public static EmailHandler instance(){
		return new EmailHandler(PropertiesUtil.getProperty("smtp.email.host"),  
				PropertiesUtil.getProperty("smtp.email.auth.user"),  
				PropertiesUtil.getProperty("smtp.email.auth.pwd")); 
	}
	
	public static EmailHandler instance(String smtpHost, String sendFrom, String sendPwd){
		return new EmailHandler(smtpHost,  sendFrom,  sendPwd);
	}
	
	public void SendEmail(String subject, String content, String toAddress) throws Exception{
		
		// 构建授权信息，用于进行SMTP进行身份验证
		Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress form = new InternetAddress(props.getProperty("mail.user"));
		message.setFrom(form);
		
        // 设置收件人
        InternetAddress to = new InternetAddress(toAddress);
		
        message.setRecipient(RecipientType.TO, to);
        
        //// 设置抄送
        //        InternetAddress cc = new InternetAddress("luo_aaaaa@yeah.net");
        //        message.setRecipient(RecipientType.CC, cc);
        //// 设置密送，其他的收件人不能看到密送的邮件地址
        //	InternetAddress bcc = new InternetAddress("aaaaa@163.com");
        //	message.setRecipient(RecipientType.CC, bcc);
        
        // 设置邮件标题
        message.setSubject(subject);
        
        // 设置邮件的内容体
        message.setContent(content, "text/html;charset=UTF-8");
        
        // 发送邮件
        Transport.send(message);
	}
}
