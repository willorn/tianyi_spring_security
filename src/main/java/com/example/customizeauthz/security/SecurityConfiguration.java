package com.example.customizeauthz.security;

import com.example.customizeauthz.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class SecurityConfiguration {

    @Value("${spring.profiles.active:prod}")  // 默认为prod环境
    private String activeProfile;

    @Resource
    private UserService userService;

    /**
     * 配置密码编码是 BCryptPasswordEncoder 算法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(new byte[16]); // 预热SecureRandom
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B, 12, secureRandom);
    }

    /**
     * AuthenticationEntryPoint 是 Spring Security 中处理认证异常的核心入口点，主要负责：
     * <p>
     * 1. 捕获未认证/认证失败的请求
     * 2. 生成统一的认证失败响应
     * 3. 提供认证异常处理的扩展点
     * </p>
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // 构建Json
            Map<String, Object> map = new HashMap<>();
            map.put("code", HttpServletResponse.SC_UNAUTHORIZED);
            map.put("msg", authException.getMessage());
            map.put("path", request.getRequestURI());
            map.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            // 返回JSON格式
            String json = new ObjectMapper().writeValueAsString(map);
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
            writer.close();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                //从Authentication对象中获取用户名和身份凭证信息
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();
                UserDetails user = userService.loadUserByUsername(username);
                if (passwordEncoder().matches(password, user.getPassword())) {
                    log.info("Access success:" + user);
                    // Case1 密码匹配成功则构建一个UsernamePasswordAuthenticationToken对象并返回
                    return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
                } else {
                    // Case2 密码匹配失败则抛出异常
                    log.error("Access denied:The username or password is wrong!");
                    throw new BadCredentialsException("The username or password is wrong!");
                }
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return authentication.equals(UsernamePasswordAuthenticationToken.class);
            }
        };
    }

    public boolean authorize(Authentication authentication, HttpServletRequest request) {
        return true;
    }

    //基于基础认证模式进行测试
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // http.authorizeHttpRequests((authz) -> {
        //     authz.anyRequest().authenticated();
        // }).httpBasic(withDefaults());
        // return http.build();
        //启用会话存储
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        http
                // authentication entry for exception handler
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())

                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/login.html").permitAll()  // 登录页面（如果不是BE写的话可以去掉这里）
                .mvcMatchers(HttpMethod.GET, "/hello").hasRole("ADMIN1")  // 完成登录认证 && 拥有ADMIN角色
                .mvcMatchers(HttpMethod.GET, "/public").access("hasAnyRole('USER', 'ADMIN')")  // 完成登录认证 && 是 ADMIN 或者是 USR
                .mvcMatchers(HttpMethod.GET, "/hello").access("hasAuthority('CREATE_USER')")  // 完成登录认证 && 拥有ADMIN角色
                .mvcMatchers(HttpMethod.GET, "/hello").access("@securityConfiguration.authorize(authentication, request)")
                .anyRequest().authenticated() // 其他请求只需要最基础的登录认证

                // For login Page启用表单认证模式 - 登录
                // - loginProcessingUrl：默认请求提交地址 - 它属于 Spring Security 的内置认证流程 这个URL会被 Spring Security 自动处理，不需要在Controller中实现
                // - defaultSuccessUrl：默认登录成功后跳转的页面
                // - permitAll：放行上面loginPage与loginProcessingUrl不做认证
                // - usernameParameter：设置提交的参数名
                .and()
                .formLogin()
                .successHandler(loginSuccessHandler())
                .failureHandler(loginFailureHandler())
                .loginProcessingUrl("/check_login")
                .permitAll()
                .usernameParameter("u").passwordParameter("p")

                // For logout Button
                .and()
                .logout()
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessHandler(logoutSuccessHandler())

                //禁用csrf安全防护
                .and()
                .csrf().ignoringAntMatchers("/ant-tokens/");
        return http.build();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":200,\"msg\":\"Logout Success\"}");
            writer.flush();
            writer.close();
        };
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":200,\"msg\":\"Login Success\"}");
            writer.flush();
            writer.close();
        };
    }

    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, exception) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":401,\"msg\":\"Login Failure\"}");
            writer.flush();
            writer.close();
        };
    }


}
