package com.healthy.diet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthy.diet.entity.AddressBook;
import com.healthy.diet.mapper.AddressBookMapper;
import com.healthy.diet.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
   implements AddressBookService{
}
