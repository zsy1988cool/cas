package com.lucky.accounts.client.handle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lucky.accounts.client.core.ConstantKeys;
import com.lucky.accounts.client.validation.AccountPrincipal;


/**
 * @Description 执行ｔｏｋｅｎ的保存和删除操作类
 * @Author seyi.zhou@luckincoffee.com
 * @Date 2020年4月11日 下午4:56:36
 */
public class OAuth2TokenHandler {
	/** 日志处理 */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    /** 此类是OAuth客户端，用于使用OAuth包装器对CA服务器上的用户进行身份验证 */
    protected CasOAuthWrapperClient client;
    
    public OAuth2TokenHandler(CasOAuthWrapperClient client) {
    	this.client = client;
    }

    /**
     * 处理ｈｔｔｐ请求中的认证
     *
     * @param request HTTP请求.
     * @param response HTTP响应.
     * @return 判断请求是否继续进行响应
     */
    public boolean doAuthorization(final HttpServletRequest request, final HttpServletResponse response)  throws Exception {
    	final HttpSession session = request.getSession(true);
		final Assertion assertion = session != null ?  (Assertion)session.getAttribute(ConstantKeys.CONST_CAS_ASSERTION) : null;

		if (assertion != null) {
			return true;
		}

		String unAuthUrl = request.getRequestURL().toString();
		logger.trace("Received a unauth request" + unAuthUrl);
		
		session.setAttribute(ConstantKeys.CONST_CAS_UNAUTH_URL, unAuthUrl);
		
		WebContext ctx = new J2EContext(request, response);
		client.redirect(ctx, false, false);
		return false;
    }
    
    /**
     * 处理ｈｔｔｐ认证请求返回
     *
     * @param request HTTP请求.
     * @param response HTTP响应.
     * @return 判断请求是否继续进行响应
     */
    public boolean doAuthorizationCallback(final HttpServletRequest request, final HttpServletResponse response)  throws Exception {
    	WebContext ctx = new J2EContext(request, response);
		OAuthCredentials credentials = client.getCredentials(ctx);
		CasOAuthWrapperProfile profile = client.getUserProfile(credentials, ctx);
		
		String authCode = credentials.getVerifier();
		String accountId = profile.getId();
		
		final HttpSession session = request.getSession(false);
		
		Assertion assertion = new AssertionImpl(new AccountPrincipal(accountId));
		session.setAttribute(ConstantKeys.CONST_CAS_ASSERTION, assertion);
		
		String unAuthUrl = (String)session.getAttribute(ConstantKeys.CONST_CAS_UNAUTH_URL);
		if(!CommonUtils.isBlank(unAuthUrl)) {
			response.sendRedirect(unAuthUrl);
			return false;
		}
		
		return true;
    }
}