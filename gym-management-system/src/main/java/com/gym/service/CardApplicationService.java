package com.gym.service;

import com.gym.pojo.CardApplication;

import java.util.List;

public interface CardApplicationService {

    List<CardApplication> findAll();

    Boolean insert(CardApplication application);

    Boolean updateStatus(CardApplication application);

    List<CardApplication> findByMemberAccount(Integer memberAccount);
}
