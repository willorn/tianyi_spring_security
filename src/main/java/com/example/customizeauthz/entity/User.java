package com.example.customizeauthz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@Data
@TableName("my_user")
public class User implements UserDetails {
    @TableId(type = IdType.NONE)
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private Integer enabled;

    /**
     * 获取用户可以访问的权限集合？用户的权限信息
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 可以从数据库里面获取到这些信息
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("CREATE_USER"),
                new SimpleGrantedAuthority("UPDATE_USER"));
    }

    /*
     * 这里的一个 API 设计思路很好
     * 所有的判断是否的逻辑返回为 true，都是可用；返回为 false，都是不可用。这种逻辑就很顺畅
     * */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled==1;
    }

}