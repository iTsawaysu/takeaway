package com.sun.takeaway.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sun
 */
@Data
public class CategoryAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private Integer type;

    private Integer sort;
}
