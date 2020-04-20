package com.lucky.sso.authentication.credential;

import org.apereo.cas.authentication.UsernamePasswordCredential;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(exclude = { "password" })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccountCredential extends UsernamePasswordCredential {
    private static final long serialVersionUID = -4166149641561667276L;
    
    private String email;
    private String telephone;
    private String capcha;
    private Boolean capchaEnabled;
    
}
