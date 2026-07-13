package com.gym.service.impl;

import com.gym.mapper.MemberMapper;
import com.gym.pojo.Member;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Member> findAll() {
        return memberMapper.findAll();
    }

    @Override
    public Boolean insertMember(Member member) {
        String encodedPassword = passwordEncoder.encode(member.getMemberPassword());
        member.setMemberPassword(encodedPassword);
        return memberMapper.insertMember(member);
    }

    @Override
    public Boolean updateMemberByMemberAccount(Member member) {
        return memberMapper.updateMemberByMemberAccount(member);
    }

    @Override
    public Member userLogin(Member member) {
        Member stored = memberMapper.selectByAccount(member.getMemberAccount());
        if (stored == null) {
            return null;
        }
        if (passwordEncoder.matches(member.getMemberPassword(), stored.getMemberPassword())) {
            return stored;
        }
        return null;
    }

    @Override
    public Boolean deleteByMemberAccount(Integer memberAccount) {
        return memberMapper.deleteByMemberAccount(memberAccount);
    }

    @Override
    public Integer selectTotalCount() {
        return memberMapper.selectTotalCount();
    }

    @Override
    public List<Member> selectByMemberAccount(Integer memberAccount) {
        return memberMapper.selectByMemberAccount(memberAccount);
    }
}
