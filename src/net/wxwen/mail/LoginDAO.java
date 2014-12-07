package net.wxwen.mail;

import java.util.Properties;

import android.R.integer;

import net.wxwen.mail.User;



public interface LoginDAO {
	/**
	 * 是否登录成功
	 * @true 登录成功
	 * @false 登录失败
	 */
	public boolean isLogin(User user,int mailType);
	/**
	 * smtp验证
	 */

	public String whichSmtp(String smtp);
     public String decideSmtp(int type);
}
