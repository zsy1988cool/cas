package com.lucky.accounts.client.core;


/**
 * @Description 帐号认证异常类
 * @Author seyi.zhou@luckincoffee.com
 * @Date 2020年4月11日 下午5:14:15
 */
public class AccountAuthorizaionException extends Exception{
	 /**
     * 序列唯一ID
     */
    private static final long serialVersionUID = -7036248720402711816L;

    /**
     * 通过提供的错误信息创建异常对象.
     *
     * @param string 异常信息说明
     */
    public AccountAuthorizaionException(final String string) {
        super(string);
    }

    /**
     * 通过提供的错误信息创建异常对象,并添加到异常对象
     *
     * @param string 异常信息说明
     * @param throwable 原始的异常对象
     */
    public AccountAuthorizaionException(final String string, final Throwable throwable) {
        super(string, throwable);
    }

    /**
     * 创建异常对象,并添加到异常对象.
     * @param 原始的异常对象.                                    
     */
    public AccountAuthorizaionException(final Throwable throwable) {
        super(throwable);
    }
}
