package com.sun.takeaway.model.dto;

import com.sun.takeaway.entity.Setmeal;
import com.sun.takeaway.entity.SetmealDish;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author sun
 */
@Data
public class SetmealDTO extends Setmeal implements Serializable {
    public static final long serialVersionUID = 1L;

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
