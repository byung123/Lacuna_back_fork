package LacunaMatata.Lacuna.config;


import LacunaMatata.Lacuna.security.filter.JwtTokenFilter;
import LacunaMatata.Lacuna.security.handler.auth.OAuth2SuccessHandler;
import LacunaMatata.Lacuna.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Autowired
    private OAuth2Service oAuth2Service;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().disable();
        http.httpBasic().disable();
        http.cors();
        http.csrf().disable();
        http.headers().frameOptions().disable();

        http.oauth2Login()
                .successHandler(oAuth2SuccessHandler)
                .userInfoEndpoint()
                .userService(oAuth2Service);

        // 기본적으로 세션이 필요한 경우에만 생성
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        http.authorizeRequests()
                .antMatchers(
                        "/api/v1/mbti/survey/**",
                        "/api/v1/mbti/survey",
                        "/api/v1/profile/header"
                ).permitAll() // 이 요청에서만 세션 생성
                .antMatchers(
                        "/test/**",
                        "/auth/**",
                        "/images/**",
                        "/swagger-ui/**",
                        "/v2/api-docs",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/api/v1/**"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
