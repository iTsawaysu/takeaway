package com.sun.takeaway.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.*;
import com.sun.takeaway.exception.ThrowUtils;
import com.sun.takeaway.mapper.OrdersMapper;
import com.sun.takeaway.service.*;
import com.sun.takeaway.utils.BaseContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author sun
 */

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private UserService userService;

    @Resource
    private AddressBookService addressBookService;

    @Resource
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     */
    @Transactional
    @Override
    public CommonResult<String> submit(Orders orders) {
        Long userId = BaseContext.get();
        List<ShoppingCart> shoppingCartList = shoppingCartService.lambdaQuery().eq(ShoppingCart::getUserId, userId).list();
        ThrowUtils.throwIf(CollectionUtil.isEmpty(shoppingCartList), ErrorCode.OPERATION_ERROR, "购物车为空，无法下单");

        // 构建订单详情信息
        // 使用 AtomicInteger 保证多线程操作下订单金额不会出现并发安全问题
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());
            orderDetail.setOrderId(IdWorker.getId());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            // 单价 * 数量（累加）
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        // 构建订单信息
        User user = userService.getById(userId);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        ThrowUtils.throwIf(addressBook == null, ErrorCode.OPERATION_ERROR, "地址信息为空，无法下单");

        orders.setNumber(String.valueOf(IdWorker.getId()));
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setPhone(user.getPhone());
        orders.setStatus(2);    // 待派送
        orders.setAmount(new BigDecimal(amount.get()));   // 总金额
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());

        // 向订单表插入数据（1条）
        boolean result = this.save(orders);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 向订单明细表插入数据（1条 或 多条）
        result = orderDetailService.saveBatch(orderDetailList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 清空购物车
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        result = shoppingCartService.remove(wrapper);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return CommonResult.success("下单成功");
    }
}
