package com.lucky.sso.authentication.handler;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import org.apereo.cas.adaptors.jdbc.QueryAndEncodeDatabaseAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

/**
 * @author Seyi.Zhou
 * @date:  2020年3月30日 上午10:47:52
 * @Description: 身份认证
 */
public class AccoutAuthenticationHandler extends QueryAndEncodeDatabaseAuthenticationHandler  {
	
	public AccoutAuthenticationHandler(final String name, final ServicesManager servicesManager, final PrincipalFactory principalFactory,
            final Integer order, final DataSource dataSource, final String algorithmName, final String sql, 
            final String passwordFieldName, final String saltFieldName, final String expiredFieldName, final String disabledFieldName,
            final String numberOfIterationsFieldName, final long numberOfIterations, final String staticSalt) {
        super(name, servicesManager, principalFactory, order, dataSource, algorithmName, sql, passwordFieldName,
        		saltFieldName, expiredFieldName, disabledFieldName, numberOfIterationsFieldName, numberOfIterations,
        		staticSalt);
    }

	@Override
	protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
			final UsernamePasswordCredential transformedCredential,
            final String originalPassword)
			throws GeneralSecurityException, PreventedException {

		if (StringUtils.isBlank(this.sql) || StringUtils.isBlank(this.algorithmName) || getJdbcTemplate() == null) {
            throw new GeneralSecurityException("Authentication handler is not configured correctly");
        }

        String loginName = transformedCredential.getUsername();
        try {
        	String querySql = this.sql.replace("?", loginName);
        	//Account account = getJdbcTemplate().queryForObject(querySql, Account.class);
            final Map<String, Object> values = getJdbcTemplate().queryForMap(querySql);
            final String digestedPassword = digestEncodedPassword(transformedCredential.getPassword(), values);

            if (!values.get(this.passwordFieldName).equals(digestedPassword)) {
                throw new FailedLoginException("Password does not match value on record.");
            }
            if (StringUtils.isNotBlank(this.expiredFieldName) && values.containsKey(this.expiredFieldName)) {
                final String dbExpired = values.get(this.expiredFieldName).toString();
                if (BooleanUtils.toBoolean(dbExpired) || "1".equals(dbExpired)) {
                    throw new AccountPasswordMustChangeException("Password has expired");
                }
            }
            if (StringUtils.isNotBlank(this.disabledFieldName) && values.containsKey(this.disabledFieldName)) {
                final String dbDisabled = values.get(this.disabledFieldName).toString();
                if (BooleanUtils.toBoolean(dbDisabled) || "1".equals(dbDisabled)) {
                    throw new AccountDisabledException("Account has been disabled");
                }
            }
            
            loginName = values.get("employee_id").toString();
            return createHandlerResult(transformedCredential, this.principalFactory.createPrincipal(loginName), new ArrayList<>(0));

        } catch (final IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0) {
                throw new AccountNotFoundException(loginName + " not found with SQL query");
            }
            throw new FailedLoginException("Multiple records found for " + loginName);
        } catch (final DataAccessException e) {
            throw new PreventedException("SQL exception while executing query for " + loginName, e);
        }
	}
}
