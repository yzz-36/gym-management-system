package com.gym.controller;

import com.gym.entity.Member;
import com.gym.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class PasswordResetController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MemberService memberService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String SESSION_VERIFY_CODE = "password_reset_code";
    private static final String SESSION_VERIFY_EMAIL = "password_reset_email";
    private static final long CODE_EXPIRE_MINUTES = 5;

    @PostMapping("/forgot-password/send-code")
    public ResponseEntity<Map<String, Object>> sendCode(String email, HttpSession session) {
        if (email == null || email.trim().isEmpty()) {
            return badRequest("邮箱不能为空");
        }
        email = email.trim();

        Member member = memberService.selectByEmail(email);
        if (member == null) {
            return badRequest("该邮箱未绑定任何账号");
        }

        String code = generateCode();
        session.setAttribute(SESSION_VERIFY_CODE, code);
        session.setAttribute(SESSION_VERIFY_EMAIL, email);
        session.setAttribute("password_reset_time", System.currentTimeMillis());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("健身房管理系统 - 密码重置验证码");
            message.setText("您的验证码是：" + code + "，有效期" + CODE_EXPIRE_MINUTES + "分钟，请勿泄露给他人。");
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "邮件发送失败：" + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "验证码已发送"));
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<Map<String, Object>> resetPassword(String email, String code, String newPassword, HttpSession session) {
        if (email == null || email.trim().isEmpty()) {
            return badRequest("邮箱不能为空");
        }
        if (code == null || code.trim().isEmpty()) {
            return badRequest("验证码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return badRequest("新密码不能为空");
        }
        if (newPassword.length() < 6) {
            return badRequest("新密码长度不能少于6位");
        }

        String sessionCode = (String) session.getAttribute(SESSION_VERIFY_CODE);
        String sessionEmail = (String) session.getAttribute(SESSION_VERIFY_EMAIL);
        Long sendTime = (Long) session.getAttribute("password_reset_time");

        if (sessionCode == null || sessionEmail == null || sendTime == null) {
            return badRequest("验证码已过期，请重新获取");
        }

        long elapsed = System.currentTimeMillis() - sendTime;
        if (elapsed > TimeUnit.MINUTES.toMillis(CODE_EXPIRE_MINUTES)) {
            clearSession(session);
            return badRequest("验证码已过期，请重新获取");
        }

        if (!sessionEmail.equalsIgnoreCase(email.trim())) {
            return badRequest("邮箱与发送验证码时不一致");
        }

        if (!sessionCode.equalsIgnoreCase(code.trim())) {
            return badRequest("验证码错误");
        }

        Member member = memberService.selectByEmail(email.trim());
        if (member == null) {
            return badRequest("该邮箱未绑定任何账号");
        }

        member.setMemberPassword(newPassword.trim());
        Boolean success = memberService.updatePasswordByMemberAccount(member);
        if (success == null || !success) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "密码重置失败，请稍后重试"));
        }

        clearSession(session);
        return ResponseEntity.ok(Map.of("success", true, "message", "密码重置成功，请使用新密码登录"));
    }

    private static String generateCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private static void clearSession(HttpSession session) {
        session.removeAttribute(SESSION_VERIFY_CODE);
        session.removeAttribute(SESSION_VERIFY_EMAIL);
        session.removeAttribute("password_reset_time");
    }

    private static ResponseEntity<Map<String, Object>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", message));
    }
}
