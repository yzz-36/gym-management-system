package com.gym.controller;

import com.gym.pojo.CardApplication;
import com.gym.pojo.ClassOrder;
import com.gym.pojo.ClassTable;
import com.gym.pojo.Member;
import com.gym.service.CardApplicationService;
import com.gym.service.ClassOrderService;
import com.gym.service.ClassTableService;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class ApiUserController {

    @Autowired
    private ClassTableService classTableService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ClassOrderService classOrderService;
    @Autowired
    private CardApplicationService cardApplicationService;

    @GetMapping("/toUserInfo")
    public Map<String, Object> toUserInfo(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("member", member);
        return resp;
    }

    @GetMapping("/toUpdateInfo")
    public Map<String, Object> toUpdateInfo(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("member", member);
        return resp;
    }

    @PostMapping("/updateInfo")
    public ResponseEntity<Map<String, Object>> updateUserInformation(HttpSession session, Member member) {
        Member member1 = (Member) session.getAttribute("user");
        if (member1 != null && member != null) {
            member.setMemberAccount(member1.getMemberAccount());
            member.setCardClass(member1.getCardClass());
            member.setCardTime(member1.getCardTime());
            member.setCardNextClass(member1.getCardNextClass());

            memberService.updateMemberByMemberAccount(member);
            session.setAttribute("user", member);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/toUserClass")
    public Map<String, Object> toUserClass(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        Integer memberAccount = member == null ? null : member.getMemberAccount();
        List<ClassOrder> classOrderList = classOrderService.selectClassOrderByMemberAccount(memberAccount);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("member", member);
        resp.put("classOrderList", classOrderList);
        return resp;
    }

    @PostMapping("/delUserClass")
    public ResponseEntity<Map<String, Object>> deleteUserClass(Integer classOrderId, HttpSession session) {
        ClassOrder order = classOrderService.selectByClassOrderId(classOrderId);
        if (order != null) {
            classOrderService.deleteByClassOrderId(classOrderId);
            memberService.refundMemberClass(order.getMemberAccount());

            Member sessionMember = (Member) session.getAttribute("user");
            if (sessionMember != null && sessionMember.getMemberAccount().equals(order.getMemberAccount())) {
                Member updated = memberService.selectByMemberAccount(order.getMemberAccount()).get(0);
                session.setAttribute("user", updated);
            }
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/toApplyClass")
    public Map<String, Object> toUserApplyClass(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        List<ClassTable> classList = classTableService.findAll();

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("member", member);
        resp.put("classList", classList);
        return resp;
    }

    @PostMapping("/applyClass")
    public ResponseEntity<Map<String, Object>> userApplyClass(Integer classId, HttpSession session) {
        ClassTable classTable = classTableService.selectByClassId(classId);
        Member member = (Member) session.getAttribute("user");

        if (classTable == null || member == null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "会话已失效或课程不存在");
            return ResponseEntity.ok(resp);
        }

        if (member.getCardNextClass() == null || member.getCardNextClass() <= 0) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "剩余课时不足，无法报名");
            return ResponseEntity.ok(resp);
        }

        String className = classTable.getClassName();
        String coach = classTable.getCoach();
        String classBegin = classTable.getClassBegin();
        String memberName = member.getMemberName();
        Integer memberAccount = member.getMemberAccount();

        ClassOrder classOrder = new ClassOrder(classId, className, coach, memberName, memberAccount, classBegin);

        ClassOrder classOrder1 = classOrderService.selectMemberByClassIdAndMemberAccount(classId, memberAccount);
        if (classOrder1 != null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "您已报名该课程，请勿重复报名");
            return ResponseEntity.ok(resp);
        }

        classOrderService.insertClassOrder(classOrder);
        Boolean deducted = memberService.deductMemberClass(memberAccount);
        if (!deducted) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "课时扣减失败");
            return ResponseEntity.ok(resp);
        }

        Member updated = memberService.selectByMemberAccount(memberAccount).get(0);
        session.setAttribute("user", updated);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "报名成功，剩余课时：" + updated.getCardNextClass());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/myCardApplications")
    public Map<String, Object> myCardApplications(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        List<CardApplication> list = cardApplicationService.findByMemberAccount(member != null ? member.getMemberAccount() : null);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("list", list);
        return resp;
    }

    @PostMapping("/applyCard")
    public ResponseEntity<Map<String, Object>> applyCard(String memberPhone, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        Map<String, Object> resp = new HashMap<>();

        if (member == null) {
            resp.put("success", false);
            resp.put("message", "请先登录");
            return ResponseEntity.ok(resp);
        }

        if (memberPhone == null || memberPhone.trim().isEmpty()) {
            resp.put("success", false);
            resp.put("message", "联系电话不能为空");
            return ResponseEntity.ok(resp);
        }

        if ("member".equals(member.getMemberType())) {
            resp.put("success", false);
            resp.put("message", "您已经是会员，无需申请办卡");
            return ResponseEntity.ok(resp);
        }

        List<CardApplication> existing = cardApplicationService.findByMemberAccount(member.getMemberAccount());
        if (existing != null && !existing.isEmpty()) {
            for (CardApplication app : existing) {
                if ("pending".equals(app.getStatus())) {
                    resp.put("success", false);
                    resp.put("message", "您已提交过申请，请等待管理员处理");
                    return ResponseEntity.ok(resp);
                }
            }
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(date);

        CardApplication application = new CardApplication();
        application.setMemberAccount(member.getMemberAccount());
        application.setMemberName(member.getMemberName());
        application.setMemberPhone(memberPhone);
        application.setApplyTime(now);
        application.setStatus("pending");

        Boolean result = cardApplicationService.insert(application);
        resp.put("success", result != null && result);
        if (result != null && result) {
            resp.put("message", "申请提交成功，请等待管理员审核");
        } else {
            resp.put("message", "申请提交失败，请稍后重试");
        }
        return ResponseEntity.ok(resp);
    }
}

