package com.gym.controller;

import com.gym.pojo.Admin;
import com.gym.pojo.CardApplication;
import com.gym.pojo.ClassOrder;
import com.gym.pojo.Equipment;
import com.gym.pojo.Member;
import com.gym.service.AdminService;
import com.gym.service.CardApplicationService;
import com.gym.service.ClassOrderService;
import com.gym.service.EmployeeService;
import com.gym.service.EquipmentService;
import com.gym.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiLoginController {

    private static final String SESSION_ADMIN = "admin";
    private static final String SESSION_USER = "user";

    private final MemberService memberService;
    private final AdminService adminService;
    private final EmployeeService employeeService;
    private final EquipmentService equipmentService;
    private final CardApplicationService cardApplicationService;
    private final ClassOrderService classOrderService;

    public ApiLoginController(
            MemberService memberService,
            AdminService adminService,
            EmployeeService employeeService,
            EquipmentService equipmentService,
            CardApplicationService cardApplicationService,
            ClassOrderService classOrderService) {
        this.memberService = memberService;
        this.adminService = adminService;
        this.employeeService = employeeService;
        this.equipmentService = equipmentService;
        this.cardApplicationService = cardApplicationService;
        this.classOrderService = classOrderService;
    }

    @PostMapping("/adminLogin")
    public ResponseEntity<Map<String, Object>> adminLogin(Admin admin, HttpSession session) {
        Admin loggedIn = adminService.adminLogin(admin);
        if (loggedIn == null) {
            return unauthorized("账号或密码有误");
        }
        putAdminMainDataInSession(session, loggedIn);

        int expiredCount = checkAndExpireMembers();
        session.setAttribute("expiredCount", expiredCount);

        return ResponseEntity.ok(singleSuccess());
    }

    @PostMapping("/userLogin")
    public ResponseEntity<Map<String, Object>> userLogin(Member member, HttpSession session) {
        Member loggedIn = memberService.userLogin(member);
        if (loggedIn == null) {
            return unauthorized("账号或密码有误");
        }

        if ("member".equals(loggedIn.getMemberType()) && loggedIn.getCardExpireTime() != null && !loggedIn.getCardExpireTime().trim().isEmpty()) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.util.Date expireDate = sdf.parse(loggedIn.getCardExpireTime());
                java.util.Date now = new java.util.Date();
                if (now.after(expireDate)) {
                    loggedIn.setMemberType("visitor");
                    loggedIn.setCardTime(null);
                    loggedIn.setCardExpireTime(null);
                    loggedIn.setCardClass(null);
                    loggedIn.setCardNextClass(null);
                    memberService.updateMemberByMemberAccount(loggedIn);

                    classOrderService.deleteByMemberAccount(loggedIn.getMemberAccount());

                    CardApplication cancelRecord = new CardApplication();
                    cancelRecord.setMemberAccount(loggedIn.getMemberAccount());
                    cancelRecord.setMemberName(loggedIn.getMemberName());
                    cancelRecord.setMemberPhone(loggedIn.getMemberPhone() != null ? String.valueOf(loggedIn.getMemberPhone()) : "");
                    cancelRecord.setApplyTime(sdf.format(now) + " " + new java.text.SimpleDateFormat("HH:mm:ss").format(now));
                    cancelRecord.setStatus("cancelled");
                    cancelRecord.setRemark("会员卡到期自动取消");
                    cancelRecord.setType("cancel");
                    cardApplicationService.insert(cancelRecord);
                }
            } catch (Exception e) {
                // ignore date parse errors
            }
        }

        session.setAttribute(SESSION_USER, loggedIn);
        return ResponseEntity.ok(singleSuccess());
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(singleSuccess());
    }

    @GetMapping("/toAdminMain")
    public ResponseEntity<Map<String, Object>> toAdminMain(HttpSession session) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);

        // 实时统计，不依赖 session 缓存
        List<Member> allMembers = memberService.findAll();
        long memberTotal = allMembers.stream().filter(m -> "member".equals(m.getMemberType())).count();
        int employeeTotal = employeeService.selectTotalCount() != null ? employeeService.selectTotalCount() : 0;
        long humanTotal = allMembers.size() + employeeTotal;
        int equipmentTotal = equipmentService.selectTotalCount() != null ? equipmentService.selectTotalCount() : 0;

        body.put("memberTotal", memberTotal);
        body.put("employeeTotal", employeeTotal);
        body.put("humanTotal", humanTotal);
        body.put("equipmentTotal", equipmentTotal);

        List<CardApplication> pendingApps = cardApplicationService.findAll().stream()
                .filter(app -> "pending".equals(app.getStatus()) && !"cancel".equals(app.getType()))
                .collect(Collectors.toList());
        body.put("pendingApplications", pendingApps);

        List<Member> recentMembers = new ArrayList<>();
        for (Member m : allMembers) {
            if ("member".equals(m.getMemberType())) {
                recentMembers.add(m);
            }
        }
        if (recentMembers.size() > 5) {
            recentMembers = recentMembers.subList(recentMembers.size() - 5, recentMembers.size());
        }
        body.put("recentMembers", recentMembers);

        List<ClassOrder> allOrders = classOrderService.findAll();
        body.put("classOrderTotal", allOrders.size());

        List<ClassOrder> recentClasses = new ArrayList<>();
        for (int i = Math.max(0, allOrders.size() - 5); i < allOrders.size(); i++) {
            recentClasses.add(allOrders.get(i));
        }
        body.put("recentClasses", recentClasses);

        List<Equipment> allEquipment = equipmentService.findAll();
        long normalCount = allEquipment.stream().filter(e -> "正常".equals(e.getEquipmentStatus())).count();
        long maintenanceCount = allEquipment.stream().filter(e -> "维修中".equals(e.getEquipmentStatus())).count();
        long damagedCount = allEquipment.stream().filter(e -> "损坏".equals(e.getEquipmentStatus())).count();
        body.put("equipmentNormalCount", normalCount);
        body.put("equipmentMaintenanceCount", maintenanceCount);
        body.put("equipmentDamagedCount", damagedCount);

        return ResponseEntity.ok(body);
    }

    @GetMapping("/toUserMain")
    public ResponseEntity<Map<String, Object>> toUserMain(HttpSession session) {
        Member member = (Member) session.getAttribute(SESSION_USER);
        if (member != null) {
            List<Member> latest = memberService.selectByMemberAccount(member.getMemberAccount());
            if (latest != null && !latest.isEmpty()) {
                member = latest.get(0);
                session.setAttribute(SESSION_USER, member);
            }
        }
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("member", member);
        return ResponseEntity.ok(body);
    }

    /** 管理员登录后写入 session：身份 + 主页统计（与原先 adminMain 依赖的 key 一致）。 */
    private void putAdminMainDataInSession(HttpSession session, Admin admin) {
        session.setAttribute(SESSION_ADMIN, admin);
        Integer memberTotal = memberService.selectTotalCount();
        Integer employeeTotal = employeeService.selectTotalCount();
        Integer humanTotal = memberTotal + employeeTotal;
        Integer equipmentTotal = equipmentService.selectTotalCount();
        session.setAttribute("memberTotal", memberTotal);
        session.setAttribute("employeeTotal", employeeTotal);
        session.setAttribute("humanTotal", humanTotal);
        session.setAttribute("equipmentTotal", equipmentTotal);
    }

    private static Map<String, Object> singleSuccess() {
        Map<String, Object> m = new HashMap<>(2);
        m.put("success", true);
        return m;
    }

    private static ResponseEntity<Map<String, Object>> unauthorized(String message) {
        Map<String, Object> m = new HashMap<>(4);
        m.put("success", false);
        m.put("message", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(m);
    }

    private int checkAndExpireMembers() {
        List<Member> members = memberService.findAll();
        if (members == null || members.isEmpty()) {
            return 0;
        }

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        int expiredCount = 0;

        for (Member member : members) {
            if (!"member".equals(member.getMemberType())) {
                continue;
            }
            String expireTime = member.getCardExpireTime();
            if (expireTime == null || expireTime.trim().isEmpty()) {
                continue;
            }
            try {
                java.util.Date expireDate = sdf.parse(expireTime);
                if (now.after(expireDate)) {
                    member.setMemberType("visitor");
                    member.setCardTime(null);
                    member.setCardExpireTime(null);
                    member.setCardClass(null);
                    member.setCardNextClass(null);
                    memberService.updateMemberByMemberAccount(member);

                    classOrderService.deleteByMemberAccount(member.getMemberAccount());

                    CardApplication cancelRecord = new CardApplication();
                    cancelRecord.setMemberAccount(member.getMemberAccount());
                    cancelRecord.setMemberName(member.getMemberName());
                    cancelRecord.setMemberPhone(member.getMemberPhone() != null ? String.valueOf(member.getMemberPhone()) : "");
                    cancelRecord.setApplyTime(sdf.format(now) + " " + new java.text.SimpleDateFormat("HH:mm:ss").format(now));
                    cancelRecord.setStatus("cancelled");
                    cancelRecord.setRemark("会员卡到期自动取消");
                    cancelRecord.setType("cancel");
                    cardApplicationService.insert(cancelRecord);

                    expiredCount++;
                }
            } catch (Exception e) {
                // ignore date parse errors
            }
        }
        return expiredCount;
    }
}