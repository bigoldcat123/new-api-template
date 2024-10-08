package com.czh.api.security.filter;

import com.alibaba.fastjson2.JSON;
import com.czh.api.common.CurrentUser;
import com.czh.api.common.CurrentUserVo;
import com.czh.api.common.R;
import com.czh.api.security.authentication.MailCodeAuthorization;
import com.czh.api.security.jwt.JwtService;
import com.czh.api.system.entity.UserAuth;
import com.czh.api.system.service.IUserAuthService;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class MailCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	@Autowired
	IUserAuthService userAuthService;

    public static final String SPRING_SECURITY_FORM_MAIL_KEY = "email";

	public static final String SPRING_SECURITY_FORM_CODE_KEY = "code";

	private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
	 new AntPathRequestMatcher("/mailcode", "POST");

	private String emailParameter = SPRING_SECURITY_FORM_MAIL_KEY;

	private String codeParameter = SPRING_SECURITY_FORM_CODE_KEY;

	private boolean postOnly = true;

	public MailCodeAuthenticationFilter() {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
	}

	@Autowired
	public MailCodeAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        setAuthenticationSuccessHandler((request, response, authentication) -> {
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");

			UserAuth userAuth = userAuthService.getUserAuthByEmail((String) authentication.getPrincipal());

			CurrentUser currentUser = userAuth.toCurrentUser();
			CurrentUserVo currentUserVo = new CurrentUserVo(currentUser, JwtService.createToken(currentUser));
            response.getWriter().write(JSON.toJSONString(R.okShow(currentUserVo,R.SHOW_SUCCESS , "登陆成功")));
         });
        setAuthenticationFailureHandler((request, response, exception) -> {
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(R.errorShow(exception.getMessage())));
         });
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		if (this.postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
		String email = obtainEmail(request);
		email = (email != null) ? email.trim() : "";
		String code = obtainCode(request);
		code = (code != null) ? code : "";
		MailCodeAuthorization mailCodeAuthorization = new MailCodeAuthorization(email,code);
		return this.getAuthenticationManager().authenticate(mailCodeAuthorization);
	}
	@Nullable
	protected String obtainCode(HttpServletRequest request) {
		return request.getParameter(this.codeParameter);
	}
	@Nullable
	protected String obtainEmail(HttpServletRequest request) {
		return request.getParameter(this.emailParameter);
	}

}
