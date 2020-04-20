package com.lucky.sso.configurer;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lucky.sso.login.webflow.action.captcha.CaptchaController;

@Configuration("accountControllerConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class AccountControllerConfigurer {
	/**
     * 验证码配置,注入bean到spring中
     */
    @Bean
    @ConditionalOnMissingBean(name = "captchaController")
    public CaptchaController captchaController() {
        return new CaptchaController();
    }
}
