package com.framework.authority.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author nbbjack
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    @SuppressWarnings("unchecked")
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return new User(userName, bCryptPasswordEncoder.encode(userName), getAuthority());
    }

    @SuppressWarnings("rawtypes")
    private List getAuthority() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
