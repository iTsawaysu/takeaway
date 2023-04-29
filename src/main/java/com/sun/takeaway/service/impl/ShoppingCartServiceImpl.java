package com.sun.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.ShoppingCart;
import com.sun.takeaway.exception.ThrowUtils;
import com.sun.takeaway.mapper.ShoppingCartMapper;
import com.sun.takeaway.service.ShoppingCartService;
import com.sun.takeaway.utils.BaseContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sun
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    /**
     * 添加到购物车中
     */
    @Override
    public CommonResult<ShoppingCart> putIntoShoppingCart(ShoppingCart shoppingCart) {
        Long userId = BaseContext.get();
        shoppingCart.setUserId(userId);
        Long id = shoppingCart.getId();

        // 判断添加的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        if (dishId == null) {
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        } else {
            wrapper.eq(ShoppingCart::getUserId, userId);
        }

        // 判断当前菜品或套餐是否已添加到购物车中
        ShoppingCart shoppingCartInDB = this.getOne(wrapper);
        if (shoppingCartInDB == null) {
            // 第一次添加
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            boolean result = this.save(shoppingCart);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            shoppingCartInDB = shoppingCart;
        } else {
            Integer number = shoppingCartInDB.getNumber();
            shoppingCartInDB.setNumber(number + 1);
            boolean result = this.updateById(shoppingCartInDB);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        }
        return CommonResult.success(shoppingCartInDB);
    }

    /**
     * 查看购物车
     */
    @Override
    public CommonResult<List<ShoppingCart>> viewShoppingCart() {
        List<ShoppingCart> shoppingCartList = this.lambdaQuery()
                .eq(ShoppingCart::getUserId, BaseContext.get())
                .orderByDesc(ShoppingCart::getCreateTime).list();
        return CommonResult.success(shoppingCartList);
    }

    /**
     * 扣减购物车中的菜品或套餐（此处直接使用 ShoppingCart 实体接收前端的请求参数 dishId 或 setmealId）
     */
    @Override
    public CommonResult<String> sub(ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        if (dishId == null) {
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        } else {
            wrapper.eq(ShoppingCart::getDishId, dishId);
        }

        // 判断该菜品或套餐在购物车中的数量是否大于 1
        ShoppingCart shoppingCartInDB = this.getOne(wrapper);
        Integer number = shoppingCartInDB.getNumber();
        boolean result = false;
        if (number > 1) {
            shoppingCartInDB.setNumber(number - 1);
            result = this.updateById(shoppingCartInDB);
        } else {
             result = this.remove(wrapper);
        }
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return CommonResult.success("移除成功");
    }


    /**
     * 清空购物车
     */
    @Override
    public CommonResult<String> clearShoppingCart() {
        List<ShoppingCart> shoppingCartList = this.lambdaQuery().eq(ShoppingCart::getUserId, BaseContext.get()).list();
        List<Long> idList = shoppingCartList.stream().map(shoppingCart -> shoppingCart.getId()).collect(Collectors.toList());
        boolean result = this.removeBatchByIds(idList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return CommonResult.success("清空成功");
    }
}
