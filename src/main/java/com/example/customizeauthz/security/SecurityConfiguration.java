package com.example.customizeauthz.security;

import com.example.customizeauthz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.SecurityFilterChain;

import javax.annotation.Resource;
import java.security.SecureRandom;

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

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                //从Authentication对象中获取用户名和身份凭证信息
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();
                UserDetails user = userService.loadUserByUsername(username);
                String encodePassword = passwordEncoder().encode(password);
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
        http.authorizeRequests()
                //任何请求必须要经过认证才可以放行
                .anyRequest().authenticated()

                .and() // For login Page
                //启用表单认证模式
                .formLogin()
                //默认登录页面
                .loginPage("/login.html")
                //默认请求提交地址 - 它属于 Spring Security 的内置认证流程 
                // 这个URL会被 Spring Security 自动处理，不需要在Controller中实现
                .loginProcessingUrl("/check_login")
                //放行上面loginPage与loginProcessingUrl不做认证
                .permitAll()
                //设置提交的参数名
                .usernameParameter("u").passwordParameter("p")

                .and()// For logout Button
                //开始设置注销功能
                .logout()
                //注销功能的 绑定的URL地址
                .logoutUrl("/logout")
                //Session直接过期
                .invalidateHttpSession(true)
                //清除认证信息
                .clearAuthentication(true)
                //注销后跳转地址
                .logoutSuccessUrl("/login.html").and()

                //禁用csrf安全防护
                .csrf().disable();
        return http.build();
    }

}