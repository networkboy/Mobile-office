package net.wxwen.mail;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;    
import java.util.Properties;   
import javax.mail.util.ByteArrayDataSource;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;    
import javax.mail.BodyPart;    
import javax.mail.Message;    
import javax.mail.MessagingException;    
import javax.mail.Multipart;    
import javax.mail.Session;    
import javax.mail.Transport;    
import javax.mail.internet.InternetAddress;    
import javax.mail.internet.MimeBodyPart;    
import javax.mail.internet.MimeMessage;    
import javax.mail.internet.MimeMultipart;  
public class SimpleMailSender  {    
	/**   
	 * 以文本格式发送邮件   
	 * @param mailInfo 待发送的邮件的信息   
	 */    
	public boolean sendTextMail(MailSenderInfo mailsend) {    
		// 判断是否需要身份认证    
		MyAuthenticator authenticator = null;    
		Properties pro = mailsend.getProperties();   
		if (mailsend.isValidate()) {    
			// 如果需要身份认证，则创建一个密码验证器    
			authenticator = new MyAuthenticator(mailsend.getUserName(), mailsend.getPassword());    
		}   
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session    
		Session sendMailSession = Session.getDefaultInstance(pro,authenticator);    
		try {    
			// 根据session创建一个邮件消息    
			Message mailMessage = new MimeMessage(sendMailSession); 
			//MultiPartEmail mailMessage=new MultiPartEmail(); 
			 DataHandler handler = new DataHandler(new ByteArrayDataSource(mailsend.getContent().getBytes(), "text/plain"));    
			// 创建邮件发送者地址    
			Address from = new InternetAddress(mailsend.getFromAddress());    
			// 设置邮件消息的发送者    
			mailMessage.setFrom(from);    
			// 创建邮件的接收者地址，并设置到邮件消息中    
			Address to = new InternetAddress(mailsend.getToAddress());    
			mailMessage.setRecipient(Message.RecipientType.TO,to);    
			// 设置邮件消息的主题    
			mailMessage.setSubject(mailsend.getSubject());    
			// 设置邮件消息发送的时间    
			mailMessage.setSentDate(new Date());    
			// 设置邮件消息的主要内容    
			mailMessage.setDataHandler(handler); 
			 
			String mailContent = mailsend.getContent();    
			//mailMessage.setText(mailContent);
			String filname=mailsend.getAttachFileNames();
			BodyPart mdp = new MimeBodyPart();
			//新建一个存放信件内容的BodyPart对象
			  mdp.setContent(mailContent, "text/html;charset=gb2312");
			  //给BodyPart对象设置内容和格式/编码方式
			  Multipart mm = new MimeMultipart();
			  //新建一个MimeMultipart对象用来存放BodyPart对
			  //象(事实上可以存放多个)
			  mm.addBodyPart(mdp);
			  //将BodyPart加入到MimeMultipart对象中(可以加入多个BodyPart)
			  //把mm作为消息对象的内容、

			  MimeBodyPart   mimebodypart1; 
			  FileDataSource
			        filedatasource; 
			        //逐个加入附件 
			        for   (int   i   =   0;   i   <   1;   i++)   { 
			            mimebodypart1   =   new   MimeBodyPart(); 
			            filedatasource   =   new   FileDataSource(filname); 
			            mimebodypart1.setDataHandler(new   DataHandler(filedatasource)); 
			            mimebodypart1.setFileName(filedatasource.getName()); 
			            mm.addBodyPart(mimebodypart1); 
			        } 
			        mailMessage.setContent(mm);  
			        mailMessage.saveChanges();
			    Transport.send(mailMessage);   
			return true;    
		} catch (MessagingException ex) {    
			ex.printStackTrace();    
		}    
		 

		return false;   
		   
	}    
	
}