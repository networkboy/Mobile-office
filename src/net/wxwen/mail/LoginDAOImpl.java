package net.wxwen.mail;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import android.R.integer;

import net.wxwen.mail.*;
/************************************************************************
 *   Intent��������Э�飨type��
 *   0��163Mail  1: 126Mail   2 :sinaMail 3��Gmail  4��QQMail  
 *   5������Mail  
/************************************************************************/

public class LoginDAOImpl implements LoginDAO{

	public LoginDAOImpl(){}

	/**
	 * ���Ƿ��ܵ�¼�ɹ�
	 */
	@Override
	public boolean isLogin(User user,int mailType) {
		//final String userEmail=user.getUserEmail();
		//final String userPwd=user.getUserPwd();
		//Thread emaillogin=new Thread();
		
		String mailSmtp = decideSmtp(mailType);
		try{
			Properties props = new Properties();	 
			props.put("mail.smtp.host",mailSmtp);  //����smtp�ķ�������ַ��smtp.126.com 
			props.put("mail.smtp.auth","true");          
			MyAuthenticator auth = new MyAuthenticator();
			Session session = Session.getInstance(props, auth);
			session.setDebug(true);  
			Transport trans = session.getTransport("smtp");  
			trans.connect(mailSmtp,user.getUserEmail(), user.getUserPwd());
		}catch(Exception e){
			return false;
		}

		return true;
	}

	/**
	 * �ж���sina����qq�ȵȵ�smtp������ 
	 */
	@Override
	public String whichSmtp(String smtp) {
		String _stmp;
		int i=smtp.indexOf('@');
		int j=smtp.indexOf('.');
		if(i>0&&j>0){
			_stmp=smtp.substring(i+1, j);
			return _stmp;
		}else{
			return null;
		}
	}

	public String decideSmtp(int type){
		String mailstr = null;

		switch (type) {
		case 0:
			mailstr = "smtp.163.com";
			break;
		case 1:
			mailstr ="smtp.126.com";
			break;	
		case 2:
			mailstr ="smtp.sina.com";
			break;
		case 3:
			mailstr ="smtp.gmail.com";
			break;		
		case 4:
			mailstr ="smtp.qq.com";
			break;
		default:
			break;
		}
		return mailstr;
	}
}
