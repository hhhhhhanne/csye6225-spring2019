package edu.neu.xswl.csye6225.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.sql.DataSource;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private BasicAuthEntryPoint basicAuthEntryPoint;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private DataSource dataSource;

    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/user/register").permitAll()
                .antMatchers("/reset").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(basicAuthEntryPoint);
    }
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/reset","/user/register");
    }

//    @Bean
//    public HttpSessionStrategy httpSessionStrategy() {
//        return new HeaderHttpSessionStrategy();
//    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("users").password(bCryptPasswordEncoder.encode("password")).roles("USER").build());
        return manager;
    }

    @Autowired
    public void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(this.dataSource).usersByUsernameQuery("select username, password, true from users where username = ?")
                .passwordEncoder(bCryptPasswordEncoder)
                .authoritiesByUsernameQuery("select username, 'USER' from users where username = ?");
    }

}
