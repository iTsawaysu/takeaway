package com.sun.takeaway.model.dto;


import com.sun.takeaway.entity.OrderDetail;
import com.sun.takeaway.entity.Orders;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author sun
 */
@Data
public class OrdersDTO extends Orders implements Serializable {
    public static final long serialVersionUID = 1L;

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
}
