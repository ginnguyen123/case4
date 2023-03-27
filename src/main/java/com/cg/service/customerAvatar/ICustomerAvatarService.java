package com.cg.service.customerAvatar;

import com.cg.model.Customer;
import com.cg.model.CustomerAvatar;
import com.cg.service.IGeneralStringService;

import java.util.Optional;

public interface ICustomerAvatarService extends IGeneralStringService<CustomerAvatar> {

    Optional<CustomerAvatar> findByCustomer(Customer customer);
}
