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
	
	/**
     * Put login lock status into flow scope.
     *
     * @param context  the context
     * @param requests the requests
     */
    public static void putLoginLocked(final RequestContext context, final Boolean locked) {
        context.getFlowScope().put(PARAMETER_LOCK, locked);
    }
}
