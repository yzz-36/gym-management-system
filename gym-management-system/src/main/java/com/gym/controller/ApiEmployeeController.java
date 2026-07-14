package com.gym.controller;

import com.gym.pojo.Employee;
import com.gym.service.EmployeeService;
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
@RequestMapping("/api/employee")
public class ApiEmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/selEmployee")
    public Map<String, Object> selectEmployee() {
        List<Employee> employeeList = employeeService.findAll();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("employeeList", employeeList);
        return resp;
    }

    @GetMapping("/toAddEmployee")
    public Map<String, Object> toAddEmployee() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @PostMapping("/addEmployee")
    public ResponseEntity<Map<String, Object>> addEmployee(Employee employee) {
        if (employee.getEmployeeName() == null || employee.getEmployeeName().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "员工姓名不能为空");
            return ResponseEntity.ok(resp);
        }
        if (employee.getEmployeeGender() == null || employee.getEmployeeGender().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "员工性别不能为空");
            return ResponseEntity.ok(resp);
        }

        Random random = new Random();
        String account1 = "1010";
        for (int i = 0; i < 5; i++) {
            account1 += random.nextInt(10);
        }
        Integer account = Integer.parseInt(account1);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowDay = simpleDateFormat.format(date);

        employee.setEmployeeAccount(account);
        employee.setEntryTime(nowDay);

        Boolean result = employeeService.insertEmployee(employee);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "新增员工失败");
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/delEmployee")
    public ResponseEntity<Map<String, Object>> deleteEmployee(Integer employeeAccount) {
        if (employeeAccount == null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "员工账号不能为空");
            return ResponseEntity.ok(resp);
        }

        Boolean result = employeeService.deleteByEmployeeAccount(employeeAccount);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "删除失败，员工不存在");
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/toUpdateEmployee")
    public Map<String, Object> toUpdateEmployee(Integer employeeAccount) {
        List<Employee> employeeList = employeeService.selectByEmployeeAccount(employeeAccount);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("employeeList", employeeList);
        return resp;
    }

    @PostMapping("/updateEmployee")
    public ResponseEntity<Map<String, Object>> updateEmployee(Employee employee) {
        if (employee.getEmployeeAccount() == null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "员工账号不能为空");
            return ResponseEntity.ok(resp);
        }
        if (employee.getEmployeeName() == null || employee.getEmployeeName().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "员工姓名不能为空");
            return ResponseEntity.ok(resp);
        }

        Boolean result = employeeService.updateEmployeeByEmployeeAccount(employee);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "更新失败");
        }
        return ResponseEntity.ok(resp);
    }
}

