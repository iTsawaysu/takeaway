package com.sun.takeaway.controller;

import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.entity.ShoppingCart;
import com.sun.takeaway.service.ShoppingCartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sun
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 添加到购物车中
     */
    @PostMapping("/add")
    public CommonResult<ShoppingCart> putIntoShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.putIntoShoppingCart(shoppingCart);
    }

    /**
     * 查看购物车
     */
    @GetMapping("/list")
    public CommonResult<List<ShoppingCart>> viewShoppingCart() {
        return shoppingCartService.viewShoppingCart();
    }

    /**
     * 扣减购物车中的菜品或套餐（此处直接使用 ShoppingCart 实体接收前端的请求参数 dishId 或 setmealId）
     */
    @PostMapping("/sub")
    public CommonResult<String> sub(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.sub(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public CommonResult<String> clearShoppingCart() {
        return shoppingCartService.clearShoppingCart();
    }
}
