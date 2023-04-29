package com.sun.takeaway.controller;

import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.entity.AddressBook;
import com.sun.takeaway.service.AddressBookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sun
 */
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    /**
     * 新增
     */
    @PostMapping
    public CommonResult<AddressBook> add(@RequestBody AddressBook addressBook) {
        return addressBookService.add(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    public CommonResult<AddressBook> setDefaultAddress(@RequestBody AddressBook addressBook) {
        return addressBookService.setDefaultAddress(addressBook);
    }

    /**
     * 根据 id 查询地址
     */
    @GetMapping("/{id}")
    public CommonResult<AddressBook> getAddressById(@PathVariable("id") Long id) {
        return addressBookService.getAddressById(id);
    }

    /**
     * 查询默认地址
     */
    @GetMapping("/default")
    public CommonResult<AddressBook> getDefaultAddress() {
        return addressBookService.getDefaultAddress();
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public CommonResult<List<AddressBook>> list(AddressBook addressBook) {
        return addressBookService.list(addressBook);
    }
}
