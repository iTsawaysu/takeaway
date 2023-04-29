package com.sun.takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.entity.AddressBook;

import java.util.List;

/**
 * @author sun
 */
public interface AddressBookService extends IService<AddressBook> {

    /**
     * 新增
     */
    CommonResult<AddressBook> add(AddressBook addressBook);

    /**
     * 设置默认地址
     */
    CommonResult<AddressBook> setDefaultAddress(AddressBook addressBook);

    /**
     * 根据 id 查询地址
     */
    CommonResult<AddressBook> getAddressById(Long id);

    /**
     * 查询默认地址
     */
    CommonResult<AddressBook> getDefaultAddress();

    /**
     * 查询指定用户的全部地址
     */
    CommonResult<List<AddressBook>> list(AddressBook addressBook);
}
