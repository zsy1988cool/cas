package com.lucky.sso.security;

import org.springframework.security.crypto.password.PasswordEncoder;
//import cas-org.apereo.cas.configuration.CasConfigurationProperties;

//import org.apereo.cas.configuration.model.support.pm.PasswordManagementProperties;
//import org.apereo.cas.services.RegisteredServicesEventListener;

import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.apache.xerces.util.SecurityManager;

import org.apereo.cas.pm.web.flow.actions.VerifyPasswordResetRequestAction;
public class LuckyPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		System.out.println("rawpass:" + rawPassword);
		return rawPassword.toString();
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		System.out.println("rawpass2:" + rawPassword);
		System.out.println("encodedPassword:" + encodedPassword);
		// TODO Auto-generated method stub
		return encodedPassword.equals(rawPassword);
	}

}
