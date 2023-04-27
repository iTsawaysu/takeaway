package com.sun.takeaway.service.impl;

import com.sun.takeaway.entity.AddressBook;
import com.sun.takeaway.mapper.AddressBookMapper;
import com.sun.takeaway.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author sun
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
