package com.ticketingapp.implementation;

import com.ticketingapp.dto.UserDTO;
import com.ticketingapp.entity.User;
import com.ticketingapp.mapper.MapperUtil;
import com.ticketingapp.service.SecurityService;
import com.ticketingapp.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SecurityServiceImpl implements SecurityService {

    private UserService userService;
    private MapperUtil mapperUtil;

    public SecurityServiceImpl(UserService userService, MapperUtil mapperUtil) {
        this.userService = userService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserDTO user = userService.findByUserName(s);
        if(user==null) throw new UsernameNotFoundException("This user does not exists");
        return new org.springframework.security.core.userdetails.User(user.getId().toString(),user.getPassword(),listAuthorities(user));
    }

    @Override
    public User loadUser(String param) {
        UserDTO user =  userService.findByUserName(param);
        return mapperUtil.convert(user,new User());
    }

    private Collection<? extends GrantedAuthority> listAuthorities(UserDTO user){
        List<GrantedAuthority> authorityList = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getDescription());
        authorityList.add(authority);
        return authorityList;
    }
}