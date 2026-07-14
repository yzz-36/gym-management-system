package com.gym.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardApplication {
    private Integer id;
    private Integer memberAccount;
    private String memberName;
    private String memberPhone;
    private String applyTime;
    private String status;
    private String remark;
}
