package com.gym.entity;

public class CardApplication {
    private Integer id;
    private Integer memberAccount;
    private String memberName;
    private String memberPhone;
    private String applyTime;
    private String status;
    private String remark;
    private String type;

    public CardApplication() {}

    public CardApplication(Integer id, Integer memberAccount, String memberName, String memberPhone, String applyTime, String status, String remark, String type) {
        this.id = id;
        this.memberAccount = memberAccount;
        this.memberName = memberName;
        this.memberPhone = memberPhone;
        this.applyTime = applyTime;
        this.status = status;
        this.remark = remark;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberAccount() {
        return memberAccount;
    }

    public void setMemberAccount(Integer memberAccount) {
        this.memberAccount = memberAccount;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
