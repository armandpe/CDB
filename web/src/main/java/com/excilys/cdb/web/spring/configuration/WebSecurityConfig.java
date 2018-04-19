package com.excilys.cdb.web.spring.configuration;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import com.excilys.cdb.constant.Spring;

@SuppressWarnings("deprecation")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private DriverManagerDataSource dataSource;
	
	public WebSecurityConfig(DriverManagerDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.jdbcAuthentication().dataSource(dataSource)
		.usersByUsernameQuery("select username, password, enabled"
				+ " from users where username=?")
		.authoritiesByUsernameQuery("select username, authority "
				+ "from authorities where username=?")
		.passwordEncoder(NoOpPasswordEncoder.getInstance());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().hasAnyRole("ADMIN", "USER").and()
		.formLogin().loginPage("/" + Spring.NAME_LOGIN).defaultSuccessUrl("/" + Spring.NAME_DASHBOARD).permitAll();
//		.and()
//		.exceptionHandling().accessDeniedPage("/" + Spring.NAME_403).and()
//		.logout().logoutUrl("/" + Spring.NAME_LOGOUT).logoutSuccessUrl("/" + Spring.NAME_LOGIN).invalidateHttpSession(true).deleteCookies();    
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring().antMatchers("/javax.faces.resource/**");
	}
}

