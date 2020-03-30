package com.lucky.sso.authentication;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.MessageDescriptor;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;

/**
 * @author Seyi.Zhou
 * @date:  2020年3月30日 上午10:47:52
 * @Description: 身份认证
 */
public class AccoutAuthentication extends AbstractUsernamePasswordAuthenticationHandler  {
	
	public AccoutAuthentication(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

	@Override
	protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
			UsernamePasswordCredential credential, String originalPassword)
			throws GeneralSecurityException, PreventedException {


		String username = credential.getUsername();
		String password = credential.getPassword();
		
		// 用户密码验证逻辑放这里
		
		//可自定义返回给客户端的多个属性信息
        HashMap<String, Object> returnInfo = new HashMap<>();
        returnInfo.put("expired", "11111");
		
        final List<MessageDescriptor> warnings = new ArrayList<>();
		return createHandlerResult(credential,
                this.principalFactory.createPrincipal(username, returnInfo), warnings);
	}
}
