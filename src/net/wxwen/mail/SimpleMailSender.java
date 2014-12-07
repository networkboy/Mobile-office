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
	 * ���ı���ʽ�����ʼ�   
	 * @param mailInfo �����͵��ʼ�����Ϣ   
	 */    
	public boolean sendTextMail(MailSenderInfo mailsend) {    
		// �ж��Ƿ���Ҫ�����֤    
		MyAuthenticator authenticator = null;    
		Properties pro = mailsend.getProperties();   
		if (mailsend.isValidate()) {    
			// �����Ҫ�����֤���򴴽�һ��������֤��    
			authenticator = new MyAuthenticator(mailsend.getUserName(), mailsend.getPassword());    
		}   
		// �����ʼ��Ự���Ժ�������֤������һ�������ʼ���session    
		Session sendMailSession = Session.getDefaultInstance(pro,authenticator);    
		try {    
			// ����session����һ���ʼ���Ϣ    
			Message mailMessage = new MimeMessage(sendMailSession); 
			//MultiPartEmail mailMessage=new MultiPartEmail(); 
			 DataHandler handler = new DataHandler(new ByteArrayDataSource(mailsend.getContent().getBytes(), "text/plain"));    
			// �����ʼ������ߵ�ַ    
			Address from = new InternetAddress(mailsend.getFromAddress());    
			// �����ʼ���Ϣ�ķ�����    
			mailMessage.setFrom(from);    
			// �����ʼ��Ľ����ߵ�ַ�������õ��ʼ���Ϣ��    
			Address to = new InternetAddress(mailsend.getToAddress());    
			mailMessage.setRecipient(Message.RecipientType.TO,to);    
			// �����ʼ���Ϣ������    
			mailMessage.setSubject(mailsend.getSubject());    
			// �����ʼ���Ϣ���͵�ʱ��    
			mailMessage.setSentDate(new Date());    
			// �����ʼ���Ϣ����Ҫ����    
			mailMessage.setDataHandler(handler); 
			 
			String mailContent = mailsend.getContent();    
			//mailMessage.setText(mailContent);
			String filname=mailsend.getAttachFileNames();
			BodyPart mdp = new MimeBodyPart();
			//�½�һ������ż����ݵ�BodyPart����
			  mdp.setContent(mailContent, "text/html;charset=gb2312");
			  //��BodyPart�����������ݺ͸�ʽ/���뷽ʽ
			  Multipart mm = new MimeMultipart();
			  //�½�һ��MimeMultipart�����������BodyPart��
			  //��(��ʵ�Ͽ��Դ�Ŷ��)
			  mm.addBodyPart(mdp);
			  //��BodyPart���뵽MimeMultipart������(���Լ�����BodyPart)
			  //��mm��Ϊ��Ϣ��������ݡ�

			  MimeBodyPart   mimebodypart1; 
			  FileDataSource
			        filedatasource; 
			        //������븽�� 
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