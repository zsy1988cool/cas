package com.lucky.cas.client.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="order")
@CrossOrigin("*")
public class OAuth2OrderController {
	
    @GetMapping("/{id}")
    public String getOrder(@PathVariable String id) {
        //for debug
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "order id : " + id;
    }
	
	@RequestMapping(value="welcome")
	public String welcome(Model model) {
//		Assertion assertion = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
//		//获取登录用户名
//		String loginName = assertion.getPrincipal().getName();
//		System.out.printf("登录用户名:%s\r\n", loginName);
//
//		String profile = getProfile();
//		String serviceUrl = getServiceUrl();
	
		model.addAttribute("username", "");
		model.addAttribute("appname", "");
		model.addAttribute("profile", "");
		model.addAttribute("serviceUrl", "");
		//return "welcome";
		return "index";
	}
	
	@GetMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws IOException{
        // session.removeAttribute(WebSecurityConfig.SESSION_KEY);
		System.out.println("准备登出");
		session.invalidate();

		String service = "";//getServiceUrl();
		String redirectUrl = "http://sso.lucky.net:8080/sso-server/logout?service=" + service + "/cas/welcome";
		//String redirectUrl = "https://sso.lucky.net:8443/sso-server/logout?service=" + service + "/cas/welcome";
		response.sendRedirect(redirectUrl);
        //return "redirect:https://sso.lucky.net:8443/cas/logout?service=" + service + "/cas/welcome";
    }
}
