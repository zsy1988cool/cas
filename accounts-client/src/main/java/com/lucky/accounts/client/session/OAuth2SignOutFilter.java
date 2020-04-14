package com.lucky.accounts.client.session;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.session.HashMapBackedSessionMappingStorage;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.util.AbstractConfigurationFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuth2SignOutFilter extends AbstractConfigurationFilter {
	public static OAuth2SignOutHandler HANDLER;

	public void init(final FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;

		if (HANDLER.process(request, response)) {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public static class OAuth2SignOutHandler  {
		 /** Logger instance */
	    private final Logger logger = LoggerFactory.getLogger(getClass());

	    /** Mapping of token IDs and session IDs to HTTP sessions */
	    private SessionMappingStorage sessionMappingStorage = new HashMapBackedSessionMappingStorage();
	    
	    /** The name of the artifact parameter.  This is used to capture the session identifier. */
	    private String artifactParameterName = Protocol.CAS2.getArtifactParameterName();

	    /** Parameter name that stores logout request for SLO */
	    private String logoutParameterName = ConfigurationKeys.LOGOUT_PARAMETER_NAME.getDefaultValue();
	    
	    private boolean eagerlyCreateSessions = true;
	    
	    private List<String> safeParameters;
	    
	    /**
	     * Determines whether the given request contains an authentication token.
	     *
	     * @param request HTTP reqest.
	     *
	     * @return True if request contains authentication token, false otherwise.
	     */
	    private boolean isTokenRequest(final HttpServletRequest request) {
	        return CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.artifactParameterName,
	                this.safeParameters));
	    }

	    /**
	     * Determines whether the given request is a CAS  logout request.
	     *
	     * @param request HTTP request.
	     *
	     * @return True if request is logout request, false otherwise.
	     */
	    private boolean isLogoutRequest(final HttpServletRequest request) {
	        if ("POST".equalsIgnoreCase(request.getMethod())) {
	            return CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.logoutParameterName,
	                    this.safeParameters));
	        }
	        
	        if ("GET".equalsIgnoreCase(request.getMethod())) {
	            return CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.logoutParameterName, this.safeParameters));
	        }
	        return false;
	    }

	    /**
	     * Process a request regarding the SLO process: record the session or destroy it.
	     *
	     * @param request the incoming HTTP request.
	     * @param response the HTTP response.
	     * @return if the request should continue to be processed.
	     */
	    public boolean process(final HttpServletRequest request, final HttpServletResponse response) {
	        if (isTokenRequest(request)) {
	            logger.trace("Received a token request");
	           // recordSession(request);
	            return true;
	        } 
	        
	        if (isLogoutRequest(request)) {
	            logger.trace("Received a logout request");
	            destroySession(request);
	            return false;
	        } 
	        logger.trace("Ignoring URI for logout: {}", request.getRequestURI());
	        return true;
	    }

	    /**
	     * Associates a token request with the current HTTP session by recording the mapping
	     * in the the configured {@link SessionMappingStorage} container.
	     * 
	     * @param request HTTP request containing an authentication token.
	     */
	    private void recordSession(final HttpServletRequest request) {
	        final HttpSession session = request.getSession(this.eagerlyCreateSessions);

	        if (session == null) {
	            logger.debug("No session currently exists (and none created).  Cannot record session information for single sign out.");
	            return;
	        }

	        final String token = CommonUtils.safeGetParameter(request, this.artifactParameterName, this.safeParameters);
	        logger.debug("Recording session for token {}", token);

	        try {
	            this.sessionMappingStorage.removeBySessionById(session.getId());
	        } catch (final Exception e) {
	            // ignore if the session is already marked as invalid. Nothing we can do!
	        }
	        sessionMappingStorage.addSessionById(token, session);
	    }

	    /**
	     * Destroys the current HTTP session for the given CAS logout request.
	     *
	     * @param request HTTP request containing a CAS logout message.
	     */
	    private void destroySession(final HttpServletRequest request) {
	    	HttpSession session = request.getSession();
	    	session.invalidate();
//	        String logoutMessage = CommonUtils.safeGetParameter(request, this.logoutParameterName, this.safeParameters);
//	        if (CommonUtils.isBlank(logoutMessage)) {
//	            logger.error("Could not locate logout message of the request from {}", this.logoutParameterName);
//	            return;
//	        }
//	        
//	        logger.trace("Logout request:\n{}", logoutMessage);
//	        final String token = XmlUtils.getTextForElement(logoutMessage, "SessionIndex");
//	        if (CommonUtils.isNotBlank(token)) {
//	            final HttpSession session = this.sessionMappingStorage.removeSessionByMappingId(token);
//
//	            if (session != null) {
//	                final String sessionID = session.getId();
//	                logger.debug("Invalidating session [{}] for token [{}]", sessionID, token);
//
//	                try {
//	                    session.invalidate();
//	                } catch (final IllegalStateException e) {
//	                    logger.debug("Error invalidating session.", e);
//	                }
//	                //this.logoutStrategy.logout(request);
//	            }
//	        }
	    }
	}
}
