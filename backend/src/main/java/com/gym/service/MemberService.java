package com.gym.service;

import com.gym.entity.Member;

import java.util.List;

public interface MemberService {

    //查询会员信息
    List<Member> findAll();

    //新增会员信息
    Boolean insertMember(Member member);

    //根据会员账号修改会员信息
    Boolean updateMemberByMemberAccount(Member member);

    //查询会员账号密码（登录）
    Member userLogin(Member member);
    //Member selectByAccountAndPassword(Member member);

    //根据会员账号删除会员信息
    Boolean deleteByMemberAccount(Integer memberAccount);

    //查询会员数
    Integer selectTotalCount();

    //根据会员账号查询会员
    List<Member> selectByMemberAccount(Integer memberAccount);

    //根据邮箱查询会员
    Member selectByEmail(String memberEmail);

    //根据会员账号更新密码
    Boolean updatePasswordByMemberAccount(Member member);

    //扣减会员剩余课时
    Boolean deductMemberClass(Integer memberAccount);

    //返还会员剩余课时
    Boolean refundMemberClass(Integer memberAccount);

}
