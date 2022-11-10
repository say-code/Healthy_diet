package com.healthy.diet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author sayCode
 * @date 2022/10/10 17:01
 * project: Heathy_diet
 * Title: EmployeeByBusiness
 * description: TODO
 */
@Data
@AllArgsConstructor
public class EmployeeByBusiness {

    Employee employee;

    private String businessName;
}
