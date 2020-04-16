package com.edu.fpt.medtest.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private Logger log = LoggerFactory.getLogger(WebSecurity.class);

    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurity(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("configure request");
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/").permitAll()
                //any one can access
                .antMatchers(HttpMethod.POST, SecurityUtils.CUSTOMER_REGISTER).permitAll()
                .antMatchers(HttpMethod.POST, SecurityUtils.NURSE_REGISTER).permitAll()
                .antMatchers(HttpMethod.POST, SecurityUtils.COORDINATOR_REGISTER).permitAll()
                .antMatchers(HttpMethod.POST, SecurityUtils.CUSTOMER_LOGIN).permitAll()
                .antMatchers(HttpMethod.POST, SecurityUtils.NURSE_LOGIN).permitAll()
                .antMatchers(HttpMethod.POST, SecurityUtils.COORDINATOR_LOGIN).permitAll()
                .antMatchers(HttpMethod.POST, SecurityUtils.FORGOT_PASSWORD).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.LIST_ARTICLE).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.LIST_TESTTYPE_TEST).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.LIST_DISTRICT).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.DETAIL_DISTRICT).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.LIST_DISTRICT_TOWN).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.LIST_TOWN).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.DETAIL_TOWN).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.DETAIL_TEST).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.LIST_TEST).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.DETAIL_ARTICLE).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.LIST_TEST_OF_LATEST_VERSION).permitAll()
                .antMatchers(HttpMethod.POST, SecurityUtils.VERIFY_PHONE_BY_SEND_OTP).permitAll()
                .antMatchers(HttpMethod.POST, SecurityUtils.VALID_PHONE_OTP).permitAll()
                .antMatchers(HttpMethod.POST, SecurityUtils.RESEND_OTP).permitAll()

                //////////////for test
                .antMatchers(HttpMethod.POST, SecurityUtils.UPLOAD_FILE).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.SAVE_FILE).permitAll()

                .anyRequest().authenticated()
                //.anyRequest().permitAll()
                .and()
                //.addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}