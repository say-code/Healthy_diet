package com.healthy.diet.dto;

import com.healthy.diet.entity.OrderDetail;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author sayCode
 * @date 2023/3/31 18:03
 * @project Heathy_diet
 * @Title OrderDetailDto
 * @description TODO
 */
@Data
@RequiredArgsConstructor
public class OrderDetailDto extends OrderDetail {

    private String dishName;

    private String businessName;
}
