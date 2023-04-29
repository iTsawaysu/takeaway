package com.sun.takeaway.model.dto;

import com.sun.takeaway.entity.Dish;
import com.sun.takeaway.entity.DishFlavor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sun
 */
@Data
public class DishDTO extends Dish implements Serializable {
    public static final long serialVersionUID = 1L;

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
