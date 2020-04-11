package com.lucky.cas.client.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin("*")
public class OAuth2CallbackController {

	@GetMapping("/oauth2callback")
    public String oauth2callback() {
		//return "welcome";
		return "index";
    }
}
