package com.gym.controller;

import com.gym.pojo.Equipment;
import com.gym.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
public class ApiEquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/selEquipment")
    public Map<String, Object> selectEquipment() {
        List<Equipment> equipmentList = equipmentService.findAll();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("equipmentList", equipmentList);
        return resp;
    }

    @PostMapping("/delEquipment")
    public ResponseEntity<Map<String, Object>> deleteEquipment(Integer equipmentId) {
        if (equipmentId == null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "器械ID不能为空");
            return ResponseEntity.ok(resp);
        }

        Boolean result = equipmentService.deleteByEquipmentId(equipmentId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "删除失败，器械不存在");
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/toUpdateEquipment")
    public Map<String, Object> toUpdateEquipment(Integer equipmentId) {
        List<Equipment> equipmentList = equipmentService.selectByEquipmentId(equipmentId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("equipmentList", equipmentList);
        return resp;
    }

    @GetMapping("/toAddEquipment")
    public Map<String, Object> toAddEquipment() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @PostMapping("/updateEquipment")
    public ResponseEntity<Map<String, Object>> updateEquipment(Equipment equipment) {
        if (equipment.getEquipmentId() == null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "器械ID不能为空");
            return ResponseEntity.ok(resp);
        }
        if (equipment.getEquipmentName() == null || equipment.getEquipmentName().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "器械名称不能为空");
            return ResponseEntity.ok(resp);
        }
        if (equipment.getEquipmentStatus() == null || equipment.getEquipmentStatus().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "器材状态不能为空");
            return ResponseEntity.ok(resp);
        }
        String status = equipment.getEquipmentStatus().trim();
        if (!"正常".equals(status) && !"损坏".equals(status) && !"维修中".equals(status)) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "器材状态只能选择：正常、损坏、维修中");
            return ResponseEntity.ok(resp);
        }

        Boolean result = equipmentService.updateEquipmentByEquipmentId(equipment);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "更新失败");
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/addEquipment")
    public ResponseEntity<Map<String, Object>> addEquipment(Equipment equipment) {
        if (equipment.getEquipmentName() == null || equipment.getEquipmentName().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "器械名称不能为空");
            return ResponseEntity.ok(resp);
        }
        if (equipment.getEquipmentLocation() == null || equipment.getEquipmentLocation().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "器械位置不能为空");
            return ResponseEntity.ok(resp);
        }
        if (equipment.getEquipmentStatus() == null || equipment.getEquipmentStatus().trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "器材状态不能为空");
            return ResponseEntity.ok(resp);
        }
        String status = equipment.getEquipmentStatus().trim();
        if (!"正常".equals(status) && !"损坏".equals(status) && !"维修中".equals(status)) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "器材状态只能选择：正常、损坏、维修中");
            return ResponseEntity.ok(resp);
        }

        Boolean result = equipmentService.insertEquipment(equipment);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result != null && result);
        if (!(result != null && result)) {
            resp.put("message", "新增器械失败");
        }
        return ResponseEntity.ok(resp);
    }
}

