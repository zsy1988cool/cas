package com.lucky.authentication;

import org.junit.Test;
import static org.junit.Assert.*;

import org.apache.shiro.crypto.hash.ConfigurableHashService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.util.ByteSource;

public class AccoutAuthenticationTest {

	private String staticSalt = "4d!2e#";
    private String algorithmName = "MD5";
    private String encodedPassword = "123456";
    private String dynaSalt = "zhangsan@luckincoffee.com";

	@Test
	public void testPassword() {
		ConfigurableHashService hashService = new DefaultHashService();
        hashService.setPrivateSalt(ByteSource.Util.bytes(this.staticSalt));
        hashService.setHashAlgorithmName(this.algorithmName);
        hashService.setHashIterations(2);
        HashRequest request = new HashRequest.Builder()
                .setSalt(dynaSalt)
                .setSource(encodedPassword)
                .build();
        String res =  hashService.computeHash(request).toHex();
        System.out.println(res);
        
        assertEquals(res, "b39320284a789e393d9e55f3f29798b0");
	}

	public AccoutAuthenticationTest() {
	}
}
