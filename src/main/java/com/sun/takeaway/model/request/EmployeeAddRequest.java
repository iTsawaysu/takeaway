package com.sun.takeaway.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sun
 */
@Data
public class EmployeeAddRequest implements Serializable {

    public static final long serialVersionUID = 1L;

    private String name;

    private String username;

    private String phone;

    private String sex;

    private String idNumber;

}
