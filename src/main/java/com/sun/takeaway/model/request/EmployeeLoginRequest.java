package com.sun.takeaway.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;
}
