package net.wxwen.mail;

import java.util.Properties;

import android.R.integer;

import net.wxwen.mail.User;



public interface LoginDAO {
	/**
	 * �Ƿ��¼�ɹ�
	 * @true ��¼�ɹ�
	 * @false ��¼ʧ��
	 */
	public boolean isLogin(User user,int mailType);
	/**
	 * smtp��֤
	 */

	public String whichSmtp(String smtp);
     public String decideSmtp(int type);
}
