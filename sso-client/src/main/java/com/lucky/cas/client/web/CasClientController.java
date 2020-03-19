package com.lucky.cas.client.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="cas")
@CrossOrigin("*")
public class CasClientController implements ApplicationContextAware {
	@Autowired
	private  HttpServletRequest request;
	
	private ApplicationContext applicationContext;
	
	String getProfile() {
		String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
		return activeProfiles[0];
	}
	
	String getPort() {
		String profile = getProfile();
		String port = "client2".equals(profile) ? "8002" : "8001";
		return port;
	}
	
	String getServiceUrl() {
		String profile = getProfile();
		String port = getPort();
		return "https://sso.lucky." + profile + ".net:" + port;
	}

	@RequestMapping(value="welcome")
	public String welcome(Model model) {
		Assertion assertion = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
		//获取登录用户名
		String loginName = assertion.getPrincipal().getName();
		System.out.printf("登录用户名:%s\r\n", loginName);

		String profile = getProfile();
		String serviceUrl = getServiceUrl();
		
		model.addAttribute("username", loginName);
		model.addAttribute("appname", profile);
		model.addAttribute("serviceUrl", serviceUrl);
		return "welcome";
	}
	
	@GetMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws IOException{
        // session.removeAttribute(WebSecurityConfig.SESSION_KEY);
		System.out.println("准备登出");
		session.invalidate();

		String service = getServiceUrl();
		String redirectUrl = "https://sso.lucky.net:8443/cas/logout?service=" + service + "/cas/welcome";
		response.sendRedirect(redirectUrl);
        //return "redirect:https://sso.lucky.net:8443/cas/logout?service=" + service + "/cas/welcome";
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
	}
}
