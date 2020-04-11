package com.lucky.cas.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;


/**
 * @Description OAuth2.0客户端配置类
 * @Author seyi.zhou@luckincoffee.com
 * @Date 2020年4月10日 下午3:02:45
 */
@Component
@ConfigurationProperties(prefix ="account.security.oauth2")
@Getter
@Setter
public class AccountOAuth2Config {
	String clientId;
	String clientSecret;
	String casServerUrlPrefix;
	String callbackUrl;
}