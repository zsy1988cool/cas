package com.lucky.sso.login.webflow.constants;

public interface AccountsConstants {

	 /**
     * The transition state 'userCheckFailure'.
     */
    String TRANSITION_ID_USER_CHECK_FAILURE = "userCheckFailure";
    
    /**
     * Action id 'usercheckExceptionHandler'.
     */
    String ACTION_ID_USERCHECK_EXCEPTION_HANDLER = "usercheckExceptionHandler";
	
	 /**
     * State id 'handleUsercheckFailure'.
     */
    String STATE_ID_HANDLE_USERCHECK_FAILURE = "handleUsercheckFailure";
    
    /**
     * The state 'viewLoginForm'.
     */
    String STATE_ID_VIEW_USERCHECK_FORM = "viewUsercheckForm";
    
    /**
     * The state 'handleLoginFailureCheck'.
     */
    String STATE_ID_HANDLE_LOGIN_FAILURE_CHECK = "handleLoginFailureCheck";
    
    /**
     * The state 'handleLoginFailureCheck'.
     */
    String ACTION_ID_HANDLE_LOGIN_FAILURE_CHECK = "handleLoginFailureCheckAction";
    
    /**
     * State id 'handleCaptchaVerify'.
     */
    String STATE_ID_HANDLE_CAPTCHA_VERIFY = "handleCaptchaVerify";
    
    /**
     * Action id 'captchaVerificationAction'.
     */
    String ACTION_ID_CAPTCHA_VERIFICATION_ACTION = "captchaVerificationAction";
    
    /**
     * The account status 'to activated'.
     */
    String ACCOUNT_STATUS_TO_ACTIVATED = "1";
    /**
     * The account status 'normal'.
     */
    String ACCOUNT_STATUS_NORMAL = "2";
    /**
     * The account status 'lock'.
     */
    String ACCOUNT_STATUS_LOCK = "3";
}
