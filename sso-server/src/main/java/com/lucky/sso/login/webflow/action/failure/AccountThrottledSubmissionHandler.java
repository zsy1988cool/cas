package com.lucky.sso.login.webflow.action.failure;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
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
 * 登陆错误审计处理器
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
    
    private final int recaptchahold;
    private final int smshold;
    private final int lockaddseconds;
    private final int locklimitseconds;

    public AccountThrottledSubmissionHandler(final int failureThreshold,
                                                            final int failureRangeInSeconds,
                                                            final DataSource dataSource, final String applicationCode,
                                                            final String sqlQueryAudit, final String authenticationFailureCode,
                                                            final int recaptchahold, final int smshold,
                                                            final int lockaddseconds, final int locklimitseconds) {
    	this.failureThreshold = failureThreshold;
    	this.applicationCode = applicationCode;
    	this.failureRangeInSeconds = failureRangeInSeconds;
        this.dataSource = dataSource;
        this.sqlQueryAudit = sqlQueryAudit;
        this.authenticationFailureCode = authenticationFailureCode;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        
        this.recaptchahold = recaptchahold;
        this.smshold = smshold;
        this.lockaddseconds = lockaddseconds;
        this.locklimitseconds = locklimitseconds;
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
    
    /**
     * Gets inlock time in range cut off date.
     *
     * @return the failure time in range cut off date
     */
    public Date getUnlockDate (Date lockedDate) {
        Instant instant = lockedDate.toInstant();
        final ZonedDateTime cutoff = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC).plusSeconds(getFailureRangeInSeconds());
        return DateTimeUtils.timestampOf(cutoff);
    }
    
    public List<Date> accessFailuresDatetimes(String username) {
    	final ClientInfo clientInfo = ClientInfoHolder.getClientInfo();
        final String remoteAddress = clientInfo.getClientIpAddress();
        Date failureCutOffDate = getFailureInRangeCutOffDate();

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

        System.out.println("dateCut:" + failureCutOffDate.toString() + "failures count:" + failuresInAudits.size());
        final List<Date> failures = failuresInAudits.stream().map(t -> new Date(t.getTime())).collect(Collectors.toList());
        return failures;
    }
}
