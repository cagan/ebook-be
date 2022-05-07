package com.cagan.library.config;

import com.cagan.library.security.AuthoritiesConstants;
import com.cagan.library.security.DomainUserDetailService;
import com.cagan.library.security.jwt.JwtConfigurer;
import com.cagan.library.security.jwt.TokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;


@AllArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final SecurityProblemSupport problemSupport;
    private final EBookProperties properties;
    private final TokenProvider tokenProvider;
    private final DomainUserDetailService userDetailService;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/swagger-ui/**")
                .antMatchers("test/**");
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(problemSupport)
                    .accessDeniedHandler(problemSupport)
                .and()
                    .headers()
                    .contentSecurityPolicy(properties.getSecurity().getContentSecurityPolicy())
                .and()
                    .referrerPolicy(ReferrerPolicyHeaderWriter. ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                .and()
                    .permissionsPolicy().policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()")
                .and()
                    .frameOptions()
                    .deny()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/api/authentication").permitAll()
                    .antMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .antMatchers("/api/**").permitAll()
                    .antMatchers("/management/health").permitAll()
                    .antMatchers("/management/health/**").permitAll()
                    .antMatchers("/management/info").permitAll()
                    .antMatchers("/management/prometheus").permitAll()
                    .antMatchers("management/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .and()
                .apply(securityConfigurerAdapter());
    }

    private JwtConfigurer securityConfigurerAdapter() {
        return new JwtConfigurer(tokenProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
