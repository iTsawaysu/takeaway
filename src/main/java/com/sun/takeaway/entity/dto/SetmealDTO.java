package com.sun.takeaway.entity.dto;

import com.sun.takeaway.entity.Setmeal;
import com.sun.takeaway.entity.SetmealDish;
import lombok.Data;
import java.util.List;

/**
 * @author sun
 */
@Data
public class SetmealDTO extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
