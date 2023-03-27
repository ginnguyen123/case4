package com.cg.service.customer;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.model.dto.CustomerCreateAvatarResDTO;
import com.cg.model.dto.CustomerDTO;
import com.cg.model.dto.CustomerResDTO;
import com.cg.model.dto.CustomerUpdateAvatarResDTO;
import com.cg.service.IGeneralService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface ICustomerService extends IGeneralService<Customer> {

    List<Customer> findAllByIdNot(Long id);

    Optional<CustomerResDTO> findCustomerResDTOById(Long id);

    CustomerUpdateAvatarResDTO update(Customer customer);

    CustomerUpdateAvatarResDTO updateWithAvatar(Customer customer, MultipartFile avatarFile) throws IOException;


    List<CustomerDTO> findAllCustomerDTO();

    List<CustomerDTO> findAllCustomerDTOIsFalse();

    List<CustomerResDTO> findAllByDeletedIsFalse();

    List<CustomerDTO> findAllRecipients(Long senderId);

    Boolean existsByEmailEquals(String email);

    CustomerCreateAvatarResDTO createWithAvatar(Customer customer, MultipartFile avatarFile);

    Deposit deposit(Deposit deposit);

    void incremenBalance(@Param("transactionAmount")BigDecimal transactionAmount, @Param("customer")Customer customer);

    void decreaseBalance(@Param("transactionAmount")BigDecimal transactionAmount, @Param("customer")Customer customer);

    Transfer transfer(Transfer transfer);

    Withdraw withdraw(Withdraw withdraw);
}
