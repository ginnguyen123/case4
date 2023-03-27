package com.cg.repository;

import com.cg.model.Customer;
import com.cg.model.CustomerAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CustomerAvatarRepository extends JpaRepository<CustomerAvatar, String> {

    Optional<CustomerAvatar> findByCustomer(Customer customer);
}
