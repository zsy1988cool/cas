package com.lucky.accounts.client.validation;

import org.jasig.cas.client.authentication.AttributePrincipalImpl;

public class AccountPrincipal extends AttributePrincipalImpl {
	private static final long serialVersionUID = -1443182634624127117L;

    /**
     * Creates a new principal with the given name.
     * @param name Principal name.
     */
    public AccountPrincipal(final String accountId) {
        super(accountId);
    }
    
    /**
     * Get account Id
     */
    public String getAccountId() {
    	return getName();
    }
}
