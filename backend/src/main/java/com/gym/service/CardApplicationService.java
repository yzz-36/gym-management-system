package com.gym.service;

import com.gym.entity.CardApplication;

import java.util.List;

public interface CardApplicationService {

    List<CardApplication> findAll();

    CardApplication findById(Integer id);

    Boolean insert(CardApplication application);

    Boolean updateStatus(CardApplication application);

    List<CardApplication> findByMemberAccount(Integer memberAccount);

    Boolean deleteByMemberAccount(Integer memberAccount);
}
