package hexlet.code.config.security;


import hexlet.code.component.JWTHelper;
import hexlet.code.filter.JWTAuthenticationFilter;
import hexlet.code.filter.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String LOGIN = "/login";

    public static final List<GrantedAuthority> DEFAULT_AUTHORITIES = List.of(new SimpleGrantedAuthority("USER"));

    private final RequestMatcher publicUrls;

    private final RequestMatcher authenticatedUrls;
    private final RequestMatcher loginRequest;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JWTHelper jwtHelper;

    public SecurityConfig(@Value("${base-url}") final String baseUrl,
                          final UserDetailsService userDetailsServiceValue,
                          final PasswordEncoder passwordEncoderValue, final JWTHelper jwtHelperValue) {
        this.loginRequest = new AntPathRequestMatcher(baseUrl + LOGIN, POST.toString());

        this.publicUrls = new OrRequestMatcher(
                loginRequest,
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER_PATH, POST.toString()),
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER_PATH, GET.toString()),
                new AntPathRequestMatcher(baseUrl + TASK_STATUS_CONTROLLER_PATH, GET.toString()),
                new NegatedRequestMatcher(new AntPathRequestMatcher(baseUrl + "/**"))
        );

        this.authenticatedUrls = new OrRequestMatcher(
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER_PATH, PUT.toString()),
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER_PATH, DELETE.toString()),
                new AntPathRequestMatcher(baseUrl + TASK_STATUS_CONTROLLER_PATH + "/**", GET.toString()),
                new AntPathRequestMatcher(baseUrl + TASK_STATUS_CONTROLLER_PATH + "/**", POST.toString()),
                new AntPathRequestMatcher(baseUrl + TASK_STATUS_CONTROLLER_PATH + "/**", PUT.toString()),
                new AntPathRequestMatcher(baseUrl + TASK_STATUS_CONTROLLER_PATH + "/**", DELETE.toString())
        );

        this.userDetailsService = userDetailsServiceValue;
        this.passwordEncoder = passwordEncoderValue;
        this.jwtHelper = jwtHelperValue;
    }

    /**
     * The base method of configuring of authentication type (for configuring AuthenticationManager needed).
     *
     * @param auth is being configured (of type AuthenticationManagerBuilder)
     */
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    /**
     * The base method of configuring authentication and authorization (Settings of the object HttpSecurity).
     *
     * @param http is being configured (of type HttpSecurity)
     */
    @Override
    public void configure(final HttpSecurity http) throws Exception {

        final var authenticationFilter = new JWTAuthenticationFilter(
                authenticationManagerBean(),
                loginRequest,
                jwtHelper
        );

        final var authorizationFilter = new JWTAuthorizationFilter(
                publicUrls,
                jwtHelper
        );

        http.csrf().disable()
                .authorizeRequests()
                .requestMatchers(publicUrls).permitAll()
                .requestMatchers(authenticatedUrls).authenticated()
                .anyRequest().authenticated()
                .and()
                .addFilter(authenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();

        http.headers().frameOptions().disable();

//        http.exceptionHandling().accessDeniedHandler( (request, response, exception) ->
//                response.sendError(HttpStatus.UNAUTHORIZED.value(), exception.getMessage()
//                ));
    }

}
