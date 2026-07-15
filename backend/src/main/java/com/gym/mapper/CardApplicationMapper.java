package com.gym.mapper;

import com.gym.entity.CardApplication;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CardApplicationMapper {

    List<CardApplication> findAll();

    CardApplication findById(Integer id);

    Boolean insert(CardApplication application);

    Boolean updateStatus(CardApplication application);

    List<CardApplication> findByMemberAccount(Integer memberAccount);

    Boolean deleteByMemberAccount(Integer memberAccount);
}
