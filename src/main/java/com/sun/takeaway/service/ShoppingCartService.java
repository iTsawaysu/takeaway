package com.sun.takeaway.service;

import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author sun
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 添加到购物车中
     */
    CommonResult<ShoppingCart> putIntoShoppingCart(ShoppingCart shoppingCart);

    /**
     * 查看购物车
     */
    CommonResult<List<ShoppingCart>> viewShoppingCart();

    /**
     * 扣减购物车中的菜品或套餐（此处直接使用 ShoppingCart 实体接收前端的请求参数 dishId 或 setmealId）
     */
    CommonResult<String> sub(ShoppingCart shoppingCart);

    /**
     * 清空购物车
     */
    CommonResult<String> clearShoppingCart();
}
