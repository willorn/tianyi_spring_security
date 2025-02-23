package com.example.customizeauthz.filter;

import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Configuration
public class UsernamePasswordAfterFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("UsernamePasswordAfterFilter After Filter in " + timestamp);

        chain.doFilter(request, response);
    }
}
