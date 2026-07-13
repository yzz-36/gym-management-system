package com.gym.service.impl;

import com.gym.mapper.AdminMapper;
import com.gym.pojo.Admin;
import com.gym.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Admin adminLogin(Admin admin) {
        Admin stored = adminMapper.selectByAccount(admin.getAdminAccount());
        if (stored == null) {
            return null;
        }
        if (passwordEncoder.matches(admin.getAdminPassword(), stored.getAdminPassword())) {
            return stored;
        }
        return null;
    }
}
