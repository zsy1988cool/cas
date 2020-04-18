package com.lucky.sso.login.logout.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.logout.LogoutExecutionPlan;
import org.apereo.cas.logout.LogoutManager;
import org.apereo.cas.logout.LogoutMessageCreator;
import org.apereo.cas.logout.SingleLogoutServiceMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lucky.sso.login.logout.message.AccountLogoutManager;
import com.lucky.sso.login.logout.message.AccountLogoutMessageCreator;

@Configuration("accountLogoutConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class AccountLogoutConfiguration {
	
	@Autowired
    private CasConfigurationProperties casProperties;
	
	@Qualifier("logoutExecutionPlan")
	@Autowired
	LogoutExecutionPlan logoutExecutionPlan;
	
	@Qualifier("defaultSingleLogoutServiceMessageHandler")
	@Autowired
	SingleLogoutServiceMessageHandler defaultSingleLogoutServiceMessageHandler;

    @Bean(name = "logoutBuilder")
    public LogoutMessageCreator logoutBuilder() {
        return new AccountLogoutMessageCreator();
    }

    @Autowired
    @Bean(name = "logoutManager")
    public LogoutManager logoutManager(@Qualifier("logoutExecutionPlan") final LogoutExecutionPlan logoutExecutionPlan) {
        return new AccountLogoutManager(logoutBuilder(), defaultSingleLogoutServiceMessageHandler,
            casProperties.getSlo().isDisabled(), logoutExecutionPlan);
    }
}
