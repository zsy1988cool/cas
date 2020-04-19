package com.lucky.sso.login.webflow.action.failure;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apereo.cas.util.DateTimeUtils;
import org.apereo.inspektr.common.web.ClientInfo;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


/**
 * 登陆错误审计拦截器
 *
 * @author Seyi.zhou
 */
@Slf4j
@ToString
@Getter
@RequiredArgsConstructor
public class AccountThrottledSubmissionHandler {
	private final DataSource dataSource;
    private final String sqlQueryAudit;
    private final JdbcTemplate jdbcTemplate;
    private final int failureRangeInSeconds;
    private final int failureThreshold;
    private final String applicationCode;
    private final String authenticationFailureCode;

    public AccountThrottledSubmissionHandler(final int failureThreshold,
                                                            final int failureRangeInSeconds,
                                                            final DataSource dataSource, final String applicationCode,
                                                            final String sqlQueryAudit, final String authenticationFailureCode) {
    	this.failureThreshold = failureThreshold;
    	this.applicationCode = applicationCode;
    	this.failureRangeInSeconds = failureRangeInSeconds;
        this.dataSource = dataSource;
        this.sqlQueryAudit = sqlQueryAudit;
        this.authenticationFailureCode = authenticationFailureCode;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }
    
    /**
     * Gets failure time in range cut off date.
     *
     * @return the failure time in range cut off date
     */
    protected Date getFailureInRangeCutOffDate() {
        final ZonedDateTime cutoff = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(getFailureRangeInSeconds());
        return DateTimeUtils.timestampOf(cutoff);
    }
    
    public List<Date> accessFailuresDatetimes(String username) {
    	final ClientInfo clientInfo = ClientInfoHolder.getClientInfo();
        final String remoteAddress = clientInfo.getClientIpAddress();
        Date failureCutOffDate = getFailureInRangeCutOffDate();
        
        
        // print 
        System.out.println("this.sqlQueryAudit:" + this.sqlQueryAudit);
        System.out.println("remoteAddress:" + remoteAddress);
        System.out.println("getUsernameParameterFromRequest:" + username);
        System.out.println("getAuthenticationFailureCode:" + getAuthenticationFailureCode());
        System.out.println("getApplicationCode:" + getApplicationCode());
        System.out.println("failureCutOffDate:" + failureCutOffDate);
        Date now= new Date(0);
        System.out.println("now:" + now);

        final List<Timestamp> failuresInAudits = this.jdbcTemplate.query(
            this.sqlQueryAudit,
            new Object[]{
                remoteAddress,
                username,
                getAuthenticationFailureCode(),
                getApplicationCode(),
                failureCutOffDate},
            new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP},
            (resultSet, i) -> resultSet.getTimestamp(1));

        System.out.println("dateCount:" + failureCutOffDate.toString() + "failures count:" + failuresInAudits.size());
        final List<Date> failures = failuresInAudits.stream().map(t -> new Date(t.getTime())).collect(Collectors.toList());
        return failures;
    }
}
