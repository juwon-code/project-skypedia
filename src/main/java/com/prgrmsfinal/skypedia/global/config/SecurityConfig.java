package com.prgrmsfinal.skypedia.global.config;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.global.constant.SecurityUriProperties;
import com.prgrmsfinal.skypedia.member.filter.JwtAuthenticationFilter;
import com.prgrmsfinal.skypedia.member.handler.AccessDeniedHandlerImpl;
import com.prgrmsfinal.skypedia.member.handler.AuthenticationEntryPointImpl;
import com.prgrmsfinal.skypedia.member.handler.AuthenticationFailureHandlerImpl;
import com.prgrmsfinal.skypedia.member.handler.AuthenticationSuccessHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableConfigurationProperties(SecurityUriProperties.class)
@EnableWebSecurity
public class SecurityConfig {
	private final AuthenticationEntryPointImpl authenticationEntryPoint;
	private final AccessDeniedHandlerImpl accessDeniedHandler;
	private final AuthenticationSuccessHandlerImpl authenticationSuccessHandler;
	private final AuthenticationFailureHandlerImpl authenticationFailureHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final SecurityUriProperties securityUriProperties;


	@Autowired
	public SecurityConfig(AuthenticationEntryPointImpl authenticationEntryPoint
			, AccessDeniedHandlerImpl accessDeniedHandler
			, AuthenticationSuccessHandlerImpl authenticationSuccessHandler
			, AuthenticationFailureHandlerImpl authenticationFailureHandler
		    , JwtAuthenticationFilter jwtAuthenticationFilter
			, SecurityUriProperties securityUriProperties
	) {
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.accessDeniedHandler = accessDeniedHandler;
		this.authenticationSuccessHandler = authenticationSuccessHandler;
		this.authenticationFailureHandler = authenticationFailureHandler;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.securityUriProperties = securityUriProperties;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(sessionConfig -> sessionConfig
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.exceptionHandling(ExceptionConfig -> ExceptionConfig
						.authenticationEntryPoint(authenticationEntryPoint)
						.accessDeniedHandler(accessDeniedHandler)
				)
				.authorizeHttpRequests(requestMatcher -> requestMatcher
						.requestMatchers(securityUriProperties.publicUri())
							.permitAll()
						.requestMatchers(securityUriProperties.userUri())
							.hasRole(RoleType.USER.toString())
						.requestMatchers(securityUriProperties.adminUri())
							.hasRole(RoleType.ADMIN.toString())
						.anyRequest()
							.authenticated()
				)
				.oauth2Login()
						.successHandler(authenticationSuccessHandler)
						.failureHandler(authenticationFailureHandler);

		httpSecurity.addFilterBefore(jwtAuthenticationFilter
				, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}
}
