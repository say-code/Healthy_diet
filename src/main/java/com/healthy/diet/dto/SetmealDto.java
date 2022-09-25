package com.healthy.diet.dto;

import com.healthy.diet.entity.Setmeal;
import com.healthy.diet.entity.SetmealDish;

import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
