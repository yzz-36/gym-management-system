package com.gym.mapper;

import com.gym.entity.ClassTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ClassTableMapper {

    //查询所有课程
    List<ClassTable> findAll();

    //根据id删除课程
    Boolean deleteClassByClassId(Integer classId);

    //添加课程
    Boolean insertClass(ClassTable classTable);

    //根据id查询课表
    ClassTable selectByClassId(Integer classId);

    //根据id删除已预约的课程
    Boolean deleteOrderByClassId(Integer classId);

    @Update("UPDATE class_table SET class_begin = #{classBegin} WHERE class_id = #{classId}")
    Boolean updateClassBeginByClassId(Integer classId, String classBegin);

}
