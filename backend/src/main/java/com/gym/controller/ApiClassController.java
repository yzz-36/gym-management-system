package com.gym.controller;

import com.gym.entity.ClassOrder;
import com.gym.entity.ClassTable;
import com.gym.entity.Member;
import com.gym.service.ClassOrderService;
import com.gym.service.ClassTableService;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/class")
public class ApiClassController {

    @Autowired
    private ClassTableService classTableService;
    @Autowired
    private ClassOrderService classOrderService;
    @Autowired
    private MemberService memberService;

    @GetMapping("/selClass")
    public Map<String, Object> selectClass() {
        List<ClassTable> classList = classTableService.findAll();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("classList", classList);
        return resp;
    }

    @GetMapping("/selClassOrder")
    public Map<String, Object> selectClassOrder(Integer classId) {
        cleanInvalidOrders();
        List<ClassOrder> classOrderList = classOrderService.selectMemberOrderList(classId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("classOrderList", classOrderList);
        return resp;
    }

    @GetMapping("/toAddClass")
    public Map<String, Object> toAddClass() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @PostMapping("/addClass")
    public ResponseEntity<Map<String, Object>> addClass(ClassTable classTable) {
        if (classTable.getClassName() == null || classTable.getClassName().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "课程名称不能为空");
            return ResponseEntity.ok(resp);
        }
        if (classTable.getCoach() == null || classTable.getCoach().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "教练姓名不能为空");
            return ResponseEntity.ok(resp);
        }
        if (classTable.getClassBegin() == null || classTable.getClassBegin().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "上课时间不能为空");
            return ResponseEntity.ok(resp);
        }

        Boolean result = classTableService.insertClass(classTable);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "新增课程失败");
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/delClass")
    public ResponseEntity<Map<String, Object>> deleteClass(Integer classId) {
        if (classId == null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "课程ID不能为空");
            return ResponseEntity.ok(resp);
        }

        Boolean result = classTableService.deleteClassByClassId(classId);
        if (result != null && result) {
            classTableService.deleteOrderByClassId(classId);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "删除失败，课程不存在");
        }
        return ResponseEntity.ok(resp);
    }

    private void cleanInvalidOrders() {
        List<ClassOrder> allOrders = classOrderService.findAll();
        if (allOrders == null || allOrders.isEmpty()) {
            return;
        }

        List<Member> members = memberService.findAll();
        Set<Integer> validMemberAccounts = new HashSet<>();
        if (members != null) {
            for (Member m : members) {
                if (m.getMemberAccount() != null && "member".equals(m.getMemberType())) {
                    validMemberAccounts.add(m.getMemberAccount());
                }
            }
        }

        for (ClassOrder order : allOrders) {
            if (order.getMemberAccount() == null || !validMemberAccounts.contains(order.getMemberAccount())) {
                classOrderService.deleteByClassOrderId(order.getClassOrderId());
            }
        }
    }
}

