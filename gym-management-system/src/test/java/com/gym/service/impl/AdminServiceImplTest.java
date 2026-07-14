package com.gym.service.impl;

import com.gym.mapper.AdminMapper;
import com.gym.pojo.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void adminLoginReturnsNullWhenAccountDoesNotExist() {
        Admin request = new Admin();
        request.setAdminAccount(1001);
        request.setAdminPassword("pw");

        when(adminMapper.selectByAccount(1001)).thenReturn(null);

        Admin result = adminService.adminLogin(request);

        assertNull(result);
        verify(adminMapper).selectByAccount(1001);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void adminLoginReturnsAdminWhenPasswordMatches() {
        Admin request = new Admin();
        request.setAdminAccount(1001);
        request.setAdminPassword("pw");

        Admin stored = new Admin();
        stored.setAdminAccount(1001);
        stored.setAdminPassword("hashedPw");

        when(adminMapper.selectByAccount(1001)).thenReturn(stored);
        when(passwordEncoder.matches("pw", "hashedPw")).thenReturn(true);

        Admin result = adminService.adminLogin(request);

        assertSame(stored, result);
        verify(passwordEncoder).matches("pw", "hashedPw");
    }

    @Test
    void adminLoginReturnsNullWhenPasswordDoesNotMatch() {
        Admin request = new Admin();
        request.setAdminAccount(1001);
        request.setAdminPassword("pw");

        Admin stored = new Admin();
        stored.setAdminAccount(1001);
        stored.setAdminPassword("hashedPw");

        when(adminMapper.selectByAccount(1001)).thenReturn(stored);
        when(passwordEncoder.matches("pw", "hashedPw")).thenReturn(false);

        Admin result = adminService.adminLogin(request);

        assertNull(result);
        verify(passwordEncoder).matches("pw", "hashedPw");
    }
}
