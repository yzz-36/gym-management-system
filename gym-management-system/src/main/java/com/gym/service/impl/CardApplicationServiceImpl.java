package com.gym.service.impl;

import com.gym.mapper.CardApplicationMapper;
import com.gym.pojo.CardApplication;
import com.gym.service.CardApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardApplicationServiceImpl implements CardApplicationService {

    @Autowired
    private CardApplicationMapper cardApplicationMapper;

    @Override
    public List<CardApplication> findAll() {
        return cardApplicationMapper.findAll();
    }

    @Override
    public CardApplication findById(Integer id) {
        return cardApplicationMapper.findById(id);
    }

    @Override
    public Boolean insert(CardApplication application) {
        return cardApplicationMapper.insert(application);
    }

    @Override
    public Boolean updateStatus(CardApplication application) {
        return cardApplicationMapper.updateStatus(application);
    }

    @Override
    public List<CardApplication> findByMemberAccount(Integer memberAccount) {
        return cardApplicationMapper.findByMemberAccount(memberAccount);
    }

    @Override
    public Boolean deleteByMemberAccount(Integer memberAccount) {
        return cardApplicationMapper.deleteByMemberAccount(memberAccount);
    }
}
