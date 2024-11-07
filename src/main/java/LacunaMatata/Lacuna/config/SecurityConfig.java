package LacunaMatata.Lacuna.config;


import LacunaMatata.Lacuna.security.filter.JwtTokenFilter;
import LacunaMatata.Lacuna.security.handler.InAccountExceptionHandler;
import LacunaMatata.Lacuna.security.handler.OAuth2SuccessHandler;
import LacunaMatata.Lacuna.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private InAccountExceptionHandler inAccountExceptionHandler;

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
                .antMatchers("/api/v1/mbti/survey/submit")
                .authenticated()
                        .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        http.exceptionHandling().authenticationEntryPoint(inAccountExceptionHandler);

        http.authorizeRequests()
                .antMatchers("/api/v1/mbti/survey/submit") // 이 요청에서만 세션 생성
                .authenticated()
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
                .authenticated();

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
