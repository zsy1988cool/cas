package com.lucky.sso.login.webflow.handler.util;

import org.springframework.webflow.execution.RequestContext;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Account utilities for the web tier.
 *
 * @author seyi.zhou
 */

@Slf4j
@UtilityClass
public class AccountWebUtils {
	
	private static final String PARAMETER_LOCK= "loginlocked";
	private static final String PARAMETER_LOCK_MESSAGE= "loginlocked_message";
	private static final String PARAMETER_CAPCHA_ENABLED= "capchaEnabled";
	
	/**
     * Put login lock status into flow scope.
     *
     * @param context  the context
     * @param requests the requests
     */
    public static void putLoginLocked(final RequestContext context, final Boolean locked, final String message) {
        context.getFlowScope().put(PARAMETER_LOCK, locked);
        context.getFlowScope().put(PARAMETER_LOCK_MESSAGE, message);
    }
    
    /**
     * Get lock status from flow scope.
     *
     * @param context  the context
     * return the locked status
     */
    public static Boolean getLoginLocked(final RequestContext context) {
        return (Boolean)context.getFlowScope().get(PARAMETER_LOCK);
    }
    
    /**
     * Put lcapcha status into flow scope.
     *
     * @param context  the context
     * @param capchaEnabled the capchaEnabled
     */
    public static void putCapchaEnabled(final RequestContext context, final Boolean capchaEnabled) {
        context.getFlowScope().put(PARAMETER_CAPCHA_ENABLED, capchaEnabled);
    }
    
    /**
     * Get lcapcha status from flow scope.
     *
     * @param context  the context
     * return the capchaEnabled
     */
    public static Boolean getCapchaEnabled(final RequestContext context) {
        return (Boolean)context.getFlowScope().get(PARAMETER_CAPCHA_ENABLED);
    }
}
