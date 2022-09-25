package com.healthy.diet.manage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sayCode
 * @date 2022/9/25 21:32
 * project: Heathy_diet
 * Title: Business
 * description: 公司信息基类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Business {

    private String businessId;

    private String businessName;

}
