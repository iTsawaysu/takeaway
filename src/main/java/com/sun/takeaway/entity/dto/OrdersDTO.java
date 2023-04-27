package com.sun.takeaway.entity.dto;


import com.sun.takeaway.entity.OrderDetail;
import com.sun.takeaway.entity.Orders;
import lombok.Data;
import java.util.List;

/**
 * @author sun
 */
@Data
public class OrdersDTO extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

}
