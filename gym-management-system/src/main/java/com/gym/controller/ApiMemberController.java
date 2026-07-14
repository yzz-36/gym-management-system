package com.gym.controller;

import com.gym.pojo.CardApplication;
import com.gym.pojo.Member;
import com.gym.service.CardApplicationService;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/member")
public class ApiMemberController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private CardApplicationService cardApplicationService;

    @GetMapping("/selMember")
    public Map<String, Object> selectMember() {
        List<Member> memberList = memberService.findAll();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("memberList", memberList);
        return resp;
    }

    @GetMapping("/toAddMember")
    public Map<String, Object> toAddMember() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @PostMapping("/addMember")
    public ResponseEntity<Map<String, Object>> addMember(Member member) {
        if (member.getMemberName() == null || member.getMemberName().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "会员姓名不能为空");
            return ResponseEntity.ok(resp);
        }
        if (member.getMemberGender() == null || member.getMemberGender().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "会员性别不能为空");
            return ResponseEntity.ok(resp);
        }
        if (member.getCardClass() == null || member.getCardClass() <= 0) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "课程数量必须大于0");
            return ResponseEntity.ok(resp);
        }

        Random random = new Random();
        String account1 = "2021";
        for (int i = 0; i < 5; i++) {
            account1 += random.nextInt(10);
        }
        Integer account = Integer.parseInt(account1);

        String password = "123456";

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowDay = simpleDateFormat.format(date);

        Integer nextClass = member.getCardClass();

        member.setMemberAccount(account);
        member.setMemberPassword(password);
        member.setCardTime(nowDay);
        member.setCardNextClass(nextClass);

        Boolean result = memberService.insertMember(member);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "新增会员失败");
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/delMember")
    public ResponseEntity<Map<String, Object>> deleteMember(Integer memberAccount) {
        if (memberAccount == null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "会员账号不能为空");
            return ResponseEntity.ok(resp);
        }

        Boolean result = memberService.deleteByMemberAccount(memberAccount);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "删除失败，会员不存在");
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/toUpdateMember")
    public Map<String, Object> toUpdateMember(Integer memberAccount) {
        List<Member> memberList = memberService.selectByMemberAccount(memberAccount);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("memberList", memberList);
        return resp;
    }

    @PostMapping("/updateMember")
    public ResponseEntity<Map<String, Object>> updateMember(Member member) {
        if (member.getMemberAccount() == null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "会员账号不能为空");
            return ResponseEntity.ok(resp);
        }
        if (member.getMemberName() == null || member.getMemberName().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "会员姓名不能为空");
            return ResponseEntity.ok(resp);
        }

        Boolean result = memberService.updateMemberByMemberAccount(member);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "更新失败");
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/toSelByCard")
    public Map<String, Object> toSelByCard() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @PostMapping("/selByCard")
    public Map<String, Object> selectByCardId(Integer memberAccount) {
        List<Member> memberList = memberService.selectByMemberAccount(memberAccount);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        if (memberList != null) {
            resp.put("memberList", memberList);
        } else {
            resp.put("noMessage", "会员卡号不存在！");
        }
        return resp;
    }

    @GetMapping("/cardApplications")
    public Map<String, Object> cardApplications() {
        List<CardApplication> list = cardApplicationService.findAll();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("list", list);
        return resp;
    }

    @PostMapping("/handleCardApplication")
    public ResponseEntity<Map<String, Object>> handleCardApplication(Integer id, String status, String remark, Integer cardClass) {
        if (id == null || status == null || status.trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "参数不能为空");
            return ResponseEntity.ok(resp);
        }

        CardApplication application = new CardApplication();
        application.setId(id);
        application.setStatus(status);
        application.setRemark(remark);

        Boolean result = cardApplicationService.updateStatus(application);

        if (result != null && result && "approved".equals(status) && cardClass != null && cardClass > 0) {
            List<CardApplication> allApps = cardApplicationService.findAll();
            Integer memberAccount = null;
            for (CardApplication app : allApps) {
                if (app.getId().equals(id)) {
                    memberAccount = app.getMemberAccount();
                    break;
                }
            }
            if (memberAccount != null) {
                List<Member> members = memberService.selectByMemberAccount(memberAccount);
                if (members != null && !members.isEmpty()) {
                    Member member = members.get(0);
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String nowDay = sdf.format(date);
                    member.setCardTime(nowDay);
                    member.setCardClass(cardClass);
                    member.setCardNextClass(cardClass);
                    member.setMemberType("member");
                    memberService.updateMemberByMemberAccount(member);
                }
            }
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "处理失败");
        }
        return ResponseEntity.ok(resp);
    }
}

