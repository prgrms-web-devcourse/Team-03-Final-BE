package com.prgrms.mukvengers.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.prgrms.mukvengers.global.security.token.service.JwtTokenProvider;

@TestConfiguration
public class JwtConfig {

	@Bean
	public JwtTokenProvider jwtTokenProvider(
		@Value("${jwt.issuer}") String issuer,
		@Value("${jwt.secret-key}") String secretKey,
		@Value("${jwt.expiry-seconds.access-token}") long accessTokenExpirySeconds
	) {
		return new JwtTokenProvider(issuer, secretKey, accessTokenExpirySeconds);
	}
}