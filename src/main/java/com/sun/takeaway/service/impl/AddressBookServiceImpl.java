package com.sun.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.AddressBook;
import com.sun.takeaway.exception.ThrowUtils;
import com.sun.takeaway.mapper.AddressBookMapper;
import com.sun.takeaway.service.AddressBookService;
import com.sun.takeaway.utils.BaseContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sun.takeaway.constant.CommonConstant.*;

/**
 * @author sun
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    /**
     * 新增
     */
    @Override
    public CommonResult<AddressBook> add(AddressBook addressBook) {
        ThrowUtils.throwIf(addressBook == null, ErrorCode.PARAMS_ERROR);
        addressBook.setUserId(BaseContext.get());
        boolean result = this.save(addressBook);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return CommonResult.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @Transactional
    @Override
    public CommonResult<AddressBook> setDefaultAddress(AddressBook addressBook) {
        ThrowUtils.throwIf(addressBook == null, ErrorCode.PARAMS_ERROR);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();

        // 将当前用户所拥有的所有地址设置为非默认地址
        // update address_book set is_default = 0 where user_id = ?;
        wrapper.eq(AddressBook::getUserId, BaseContext.get());
        wrapper.set(AddressBook::getIsDefault, ZERO);
        boolean result = this.update(wrapper);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 将当前地址设置为默认地址
        // update address_book set is_default = 1 where id = ?;
        addressBook.setIsDefault(ONE);
        this.updateById(addressBook);
        return CommonResult.success(addressBook);
    }

    /**
     * 根据 id 查询地址
     */
    @Override
    public CommonResult<AddressBook> getAddressById(Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        AddressBook addressBook = this.getById(id);
        ThrowUtils.throwIf(addressBook == null, ErrorCode.NOT_FOUND_ERROR);
        return CommonResult.success(addressBook);
    }

    /**
     * 查询默认地址（is_default = 1）
     */
    @Override
    public CommonResult<AddressBook> getDefaultAddress() {
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getIsDefault, ONE);
        wrapper.eq(AddressBook::getUserId, BaseContext.get());
        AddressBook addressBook = this.getOne(wrapper);
        ThrowUtils.throwIf(addressBook == null, ErrorCode.NOT_FOUND_ERROR);
        return CommonResult.success(addressBook);
    }

    /**
     * 查询指定用户的全部地址
     */
    @Override
    public CommonResult<List<AddressBook>> list(AddressBook addressBook) {
        ThrowUtils.throwIf(addressBook == null, ErrorCode.PARAMS_ERROR);

        addressBook.setUserId(BaseContext.get());

        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        List<AddressBook> addressBookList = this.list(wrapper);
        ThrowUtils.throwIf(addressBookList.isEmpty(), ErrorCode.NOT_FOUND_ERROR);
        return CommonResult.success(addressBookList);
    }
}
