package com.example.customizeauthz.filter;

import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Configuration
public class UsernamePasswordBeforeFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("UsernamePasswordBeforeFilter Before Filter in " + timestamp);

        chain.doFilter(request, response);
    }
}
