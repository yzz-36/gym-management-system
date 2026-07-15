package com.gym.service.impl;

import com.gym.mapper.EquipmentMapper;
import com.gym.entity.Equipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EquipmentServiceImplTest {

    @Mock
    private EquipmentMapper equipmentMapper;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllDelegatesToMapper() {
        Equipment equipment = new Equipment(1, "Treadmill", "Room A", "Available", "Good");
        when(equipmentMapper.findAll()).thenReturn(List.of(equipment));

        List<Equipment> result = equipmentService.findAll();

        assertEquals(1, result.size());
        assertSame(equipment, result.get(0));
        verify(equipmentMapper).findAll();
    }

    @Test
    void deleteByEquipmentIdDelegatesToMapper() {
        when(equipmentMapper.deleteByEquipmentId(7)).thenReturn(true);

        Boolean result = equipmentService.deleteByEquipmentId(7);

        assertTrue(result);
        verify(equipmentMapper).deleteByEquipmentId(7);
    }

    @Test
    void insertEquipmentDelegatesToMapper() {
        Equipment equipment = new Equipment(2, "Bench", "Room B", "Maintenance", "Needs repair");
        when(equipmentMapper.insertEquipment(equipment)).thenReturn(true);

        Boolean result = equipmentService.insertEquipment(equipment);

        assertTrue(result);
        verify(equipmentMapper).insertEquipment(equipment);
    }

    @Test
    void updateEquipmentByEquipmentIdDelegatesToMapper() {
        Equipment equipment = new Equipment(3, "Rowing Machine", "Room C", "Available", "Excellent");
        when(equipmentMapper.updateEquipmentByEquipmentId(equipment)).thenReturn(true);

        Boolean result = equipmentService.updateEquipmentByEquipmentId(equipment);

        assertTrue(result);
        verify(equipmentMapper).updateEquipmentByEquipmentId(equipment);
    }

    @Test
    void selectByEquipmentIdDelegatesToMapper() {
        Equipment equipment = new Equipment(4, "Dumbbell", "Room D", "Available", "New");
        when(equipmentMapper.selectByEquipmentId(4)).thenReturn(List.of(equipment));

        List<Equipment> result = equipmentService.selectByEquipmentId(4);

        assertEquals(List.of(equipment), result);
        verify(equipmentMapper).selectByEquipmentId(4);
    }

    @Test
    void selectTotalCountDelegatesToMapper() {
        when(equipmentMapper.selectTotalCount()).thenReturn(12);

        Integer result = equipmentService.selectTotalCount();

        assertEquals(12, result);
        verify(equipmentMapper).selectTotalCount();
    }
}
