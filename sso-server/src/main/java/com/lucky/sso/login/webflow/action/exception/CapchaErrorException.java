package com.lucky.sso.login.webflow.action.exception;

import org.apereo.cas.authentication.AuthenticationException;


/**
 * @Description 二维码验证失败异常类
 * @Author seyi.zhou@luckincoffee.com
 * @Date 2020年4月20日 下午4:45:12
 */
public class CapchaErrorException extends AuthenticationException {
	/**
	 * Serialization metadata. 
	 */
	private static final long serialVersionUID = -5032827784134751797L;

	public CapchaErrorException(){
        super();
    }

    public CapchaErrorException(String msg) {
        super(msg);
    }
}
