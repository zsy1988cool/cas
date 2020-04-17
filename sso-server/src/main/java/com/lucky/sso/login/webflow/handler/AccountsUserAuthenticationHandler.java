package com.lucky.sso.login.webflow.handler;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Map;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;

import org.apereo.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.lucky.sso.login.webflow.constants.AccountsConstants;

public class AccountsUserAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

	public AccountsUserAuthenticationHandler(String name, ServicesManager servicesManager,
			PrincipalFactory principalFactory, Integer order, DataSource dataSource) {
		super(name, servicesManager, principalFactory, order, dataSource);
	}

	@Override
	protected AuthenticationHandlerExecutionResult doAuthentication(final Credential credential)
			throws GeneralSecurityException, PreventedException {
		if (getJdbcTemplate() == null) {
			throw new GeneralSecurityException("Authentication handler is not configured correctly");
		}

		UsernamePasswordCredential usernameCredential = (UsernamePasswordCredential) credential;
		final String loginName = usernameCredential.getUsername();
		try {
			String querySql = "select * from t_basic_user_account where email=" + loginName + " or phonenumber="
					+ loginName + " or employee_id=" + loginName;
			// Account account = getJdbcTemplate().queryForObject(querySql, Account.class);
			final Map<String, Object> values = getJdbcTemplate().queryForMap(querySql);
			if (values.isEmpty())
				throw new AccountNotFoundException(loginName + " not found!");

			// 密码过期判断 throw new AccountPasswordMustChangeException("Password has expired");
			// 锁定不判断 AccountLockedException
			// 禁用判断 new AccountDisabledException("Account has been disabled");
			final String status = values.get("status").toString();
			if (AccountsConstants.ACCOUNT_STATUS_TO_ACTIVATED.equals(status)) {
				throw new AccountDisabledException("Account " + loginName + " is to be activated");
			} else if (AccountsConstants.ACCOUNT_STATUS_NORMAL.equals(status)
					|| AccountsConstants.ACCOUNT_STATUS_LOCK.equals(status)) {
				return createHandlerResult(credential, this.principalFactory.createPrincipal(loginName),
						new ArrayList<>(0));
			} else {
				// 其他状态，全部当作禁用
				throw new AccountDisabledException("Account " + loginName + " has been disabled");
			}
			// throw new AccountLockedException();
		} catch (final IncorrectResultSizeDataAccessException e) {
			if (e.getActualSize() == 0) {
				throw new AccountNotFoundException(loginName + " not found with SQL query");
			}
		}
		throw new FailedLoginException("Login Failed");
	}

	@Override
	protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
			UsernamePasswordCredential credential, String originalPassword)
			throws GeneralSecurityException, PreventedException {
		return null;
	}
}
