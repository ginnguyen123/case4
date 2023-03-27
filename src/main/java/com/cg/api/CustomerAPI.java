package com.cg.api;


import com.cg.exception.DataInputException;
import com.cg.exception.EmailExistsException;
import com.cg.exception.ResourceNotFoundException;
import com.cg.model.*;
import com.cg.model.dto.*;

import com.cg.service.customer.ICustomerService;
import com.cg.service.customerAvatar.ICustomerAvatarService;
import com.cg.service.locationRegion.ILocationRegionService;
import com.cg.service.uploadMedia.UploadService;
import com.cg.utils.AppUtils;

import com.cg.utils.UploadUtils;
import org.slf4j.ILoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerAPI {

    @Autowired
    private ICustomerService customerService;
    @Autowired
    private ILocationRegionService locationRegionService;
    @Autowired
    private AppUtils appUtils;
    @Autowired
    private ICustomerAvatarService customerAvatarService;
    @Autowired
    private UploadUtils uploadUtils;
    @Autowired
    private UploadService uploadService;
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> listCustomer(){

//        List<CustomerDTO> customers = customerService.findAllCustomerDTOIsFalse();

        List<CustomerResDTO> customerResDTOS = customerService.findAllByDeletedIsFalse();
        List<CustomerDTO> customerDTOS = new ArrayList<>();
//
        for (CustomerResDTO item : customerResDTOS) {
            CustomerAvatarDTO customerAvatarDTO = new CustomerAvatarDTO();
            customerAvatarDTO.setId(item.getAvatarId());
            customerAvatarDTO.setFileFolder(item.getFileFolder());
            customerAvatarDTO.setFileName(item.getFileName());
            customerAvatarDTO.setFileUrl(item.getFileUrl());
            CustomerDTO customerDTO = item.toCustomerDTO(customerAvatarDTO);
            customerDTOS.add(customerDTO);
        }

        return new ResponseEntity<>(customerDTOS, HttpStatus.OK);

    }
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getById(@PathVariable Long customerId) {

        Optional<CustomerResDTO> customerResDTO = customerService.findCustomerResDTOById(customerId);

        if (!customerResDTO.isPresent()) {
            throw new ResourceNotFoundException("Customer not valid");
        }

        CustomerAvatarDTO customerAvatarDTO = new CustomerAvatarDTO();
        customerAvatarDTO.setId(customerResDTO.get().getAvatarId());
        customerAvatarDTO.setFileFolder(customerResDTO.get().getFileFolder());
        customerAvatarDTO.setFileName(customerResDTO.get().getFileName());
        customerAvatarDTO.setFileUrl(customerResDTO.get().getFileUrl());

//        Customer customer = customerOptional.get();
        CustomerDTO customerDTO = customerResDTO.get().toCustomerDTO(customerAvatarDTO);

        return new ResponseEntity<>(customerDTO, HttpStatus.OK);

    }

    @GetMapping("/transfer/{senderId}")
    public ResponseEntity<List<CustomerDTO>> getAllRecipients(@PathVariable Long senderId){
        Optional<Customer> optionalCustomer = customerService.findById(senderId);

        if (!optionalCustomer.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<CustomerDTO> recipients = customerService.findAllRecipients(senderId);

        return new ResponseEntity<>(recipients,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CustomerDTO customerDTO, BindingResult bindingResult){

        new CustomerDTO().validate(customerDTO,bindingResult);

        if (bindingResult.hasFieldErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }

        Boolean existEmail = customerService.existsByEmailEquals(customerDTO.getEmail());

        if (existEmail){
            throw new EmailExistsException("The email is exist");
        }

        customerDTO.setId(null);
        customerDTO.setBalance(BigDecimal.ZERO);
        customerDTO.getLocationRegion().setId(null);

        Customer customer = customerDTO.toCustomer();


        customerService.save(customer);

        customerDTO = customer.toCustomerDTO();

        return new ResponseEntity<>(customerDTO, HttpStatus.OK);
    }

    @PostMapping("/create-with-avatar")
    public ResponseEntity<?> createWithAvatar(CustomerCreateAvatarReqDTO customerCreateAvatarReqDTO){

        MultipartFile file = customerCreateAvatarReqDTO.getFile();

        LocationRegionDTO locationRegionDTO = customerCreateAvatarReqDTO.toLocationRegionDTO();
        CustomerDTO customerDTO = customerCreateAvatarReqDTO.toCustomerDTO(locationRegionDTO);


        if (file != null) {
            Customer customer = customerDTO.toCustomer();
            CustomerCreateAvatarResDTO customerCreateAvatarResDTO = customerService.createWithAvatar(customer, file);

            return new ResponseEntity<>(customerCreateAvatarResDTO, HttpStatus.CREATED);
        }
        else {
            customerDTO.setId(null);
            customerDTO.setBalance(BigDecimal.ZERO);
            customerDTO.getLocationRegion().setId(null);

            Customer customer = customerDTO.toCustomer();
            customer = customerService.save(customer);

            customerDTO = customer.toCustomerDTO();

            return new ResponseEntity<>(customerDTO, HttpStatus.CREATED);
        }
    }

    @PatchMapping("/update-with-avatar/{customerId}")
    public ResponseEntity<?> updateWithAvatar(@PathVariable Long customerId, MultipartFile file, CustomerUpdateReqDTO customerUpdateReqDTO, BindingResult bindingResult) throws IOException {
        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            throw new ResourceNotFoundException("Customer not found");
        }

        if (file == null) {
            LocationRegionDTO locationRegionDTO = customerUpdateReqDTO.toLocationRegionDTO();

            CustomerDTO customerDTO = customerUpdateReqDTO.toCustomerDTO(locationRegionDTO);
            Customer customer = customerDTO.toCustomer();
            customer.setId(customerId);

            CustomerUpdateAvatarResDTO customerUpdateAvatarResDTO = customerService.update(customer);
            return new ResponseEntity<>(customerUpdateAvatarResDTO, HttpStatus.OK);
        }
        else {
            LocationRegionDTO locationRegionDTO = customerUpdateReqDTO.toLocationRegionDTO();

            CustomerDTO customerDTO = customerUpdateReqDTO.toCustomerDTO(locationRegionDTO);
            Customer customer = customerDTO.toCustomer();
            customer.setId(customerId);

            CustomerUpdateAvatarResDTO customerUpdateAvatarResDTO = customerService.updateWithAvatar(customer, file);

            return new ResponseEntity<>(customerUpdateAvatarResDTO, HttpStatus.OK);
        }
    }




    @DeleteMapping("/delete-avatar/{customerId}")
    public ResponseEntity<?> deleteAvatar(@PathVariable Long customerId) throws IOException {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            throw new ResourceNotFoundException("Customer invalid");
        }

        Optional<CustomerAvatar> customerAvatar = customerAvatarService.findByCustomer(customerOptional.get());

        String publicId = customerAvatar.get().getCloudId();

        uploadService.destroyImage(publicId, uploadUtils.buildImageUploadParams(customerAvatar.get()));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{customerId}")
    public ResponseEntity<?> update(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO, BindingResult bindingResult){

//        Optional<Customer> optionalCustomer = customerService.findById(customerId);

        Optional<CustomerResDTO> optionalCustomerResDTO = customerService.findCustomerResDTOById(customerId);

        if (!optionalCustomerResDTO.isPresent()){
            return new ResponseEntity<>("Customer is not exist",HttpStatus.NOT_FOUND);
        }

        new CustomerDTO().validate(customerDTO,bindingResult);

        if (bindingResult.hasFieldErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }

//        Customer customer = optionalCustomer.get();



//        customerService.save(customer);
//
//        Customer newCustomer = customerService.findById(customerId).get();
//        customerDTO = newCustomer.toCustomerDTO();

        return new ResponseEntity<>(customerDTO,HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> delete(@PathVariable Long customerId){

        Optional<Customer> optionalCustomer = customerService.findById(customerId);

        if (!optionalCustomer.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Customer customer = optionalCustomer.get();

        customer.setDeleted(true);

        customerService.save(customer);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/deposit/{customerId}")
    public ResponseEntity<?> deposit(@PathVariable Long customerId, @RequestBody DepositDTO depositDTO){

        Optional<Customer> optionalCustomer = customerService.findById(customerId);

        if (!optionalCustomer.isPresent()){
            return new ResponseEntity<>("Customer is not exist",HttpStatus.NOT_FOUND);
        }

        try{
            Long num = Long.parseLong(depositDTO.getTransactionAmount());
            if (num < 20000 || num > 1000000000){

                return new ResponseEntity<>("Amount must be more than 20000", HttpStatus.NOT_ACCEPTABLE);
            }
            if (num > 1000000000){

                return new ResponseEntity<>("Amount must be less than 1000000000", HttpStatus.NOT_ACCEPTABLE);
            }
        }catch (Exception parseException){

            return new ResponseEntity<>("The mount must be number",HttpStatus.NOT_ACCEPTABLE);
        }

        BigDecimal transactionAmount = BigDecimal.valueOf(Long.valueOf(depositDTO.getTransactionAmount()));

        Customer customer = optionalCustomer.get();

        Deposit deposit = new Deposit();
        deposit.setTransactionAmount(transactionAmount);
        deposit.setCustomer(customer);

        customerService.deposit(deposit);

        Optional<Customer> newOptionalCustomer = customerService.findById(customerId);
        Customer newCustomer = newOptionalCustomer.get();

        CustomerDTO customerDTO = newCustomer.toCustomerDTO();

        return new ResponseEntity<>(customerDTO,HttpStatus.OK);
    }

    @PostMapping("/withdraw/{customerId}")
    public ResponseEntity<?> withdraw(@PathVariable Long customerId, @RequestBody WithdrawDTO withdrawDTO){

        Optional<Customer> optionalCustomer = customerService.findById(customerId);

        if (!optionalCustomer.isPresent()){
            return new ResponseEntity<>("Customer is not exist",HttpStatus.NOT_FOUND);
        }

        try{
            Long num = Long.parseLong(withdrawDTO.getTransAmount());
        }catch (Exception e){
            return new ResponseEntity<>("The mount must be number", HttpStatus.NOT_ACCEPTABLE);
        }

        BigDecimal transactionAmount = BigDecimal.valueOf(Long.valueOf(withdrawDTO.getTransAmount()));

        if (transactionAmount.compareTo(BigDecimal.ZERO)<0){
            return new ResponseEntity<>("Not Acceptable",HttpStatus.NOT_ACCEPTABLE);
        }

        if (transactionAmount.compareTo(optionalCustomer.get().getBalance())>0){
            return new ResponseEntity<>("Insufficient balance",HttpStatus.NOT_ACCEPTABLE);
        }

        Customer customer = optionalCustomer.get();

        Withdraw withdraw = new Withdraw();
        withdraw.setCustomer(customer);
        withdraw.setTransAmount(transactionAmount);

        customerService.withdraw(withdraw);

        Optional<Customer> newOptionalCustomer = customerService.findById(customerId);

        CustomerDTO customerDTO = newOptionalCustomer.get().toCustomerDTO();

        return new ResponseEntity<>(customerDTO,HttpStatus.OK);
    }

    @PostMapping("/transfer/{senderId}")
    public  ResponseEntity<?> transfer(@PathVariable Long senderId,@Validated @RequestBody TransferDTO transferDTO){

        if (senderId == Long.parseLong(transferDTO.getRecipientId())){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        Optional<Customer> optionalSender = customerService.findById(senderId);

        if (!optionalSender.isPresent()){
            return new ResponseEntity<>("Customer is not exist",HttpStatus.NOT_FOUND);
        }

        Optional<Customer> optionalRecipient = customerService.findById(Long.valueOf(transferDTO.getRecipientId()));

        if (!optionalRecipient.isPresent()){
            return new ResponseEntity<>("Customer is not exist",HttpStatus.NOT_FOUND);
        }

        try{
            Long numAmount = Long.parseLong(transferDTO.getTransferAmount());
            Long numFees = Long.parseLong(transferDTO.getFees());
        }catch (Exception e){
            return new ResponseEntity<>("Amount must be the number", HttpStatus.NOT_ACCEPTABLE);
        }

        Customer sender = optionalSender.get();
        Customer recipient = optionalRecipient.get();

        BigDecimal transferAmount = BigDecimal.valueOf(Long.valueOf(transferDTO.getTransferAmount()));

        if(transferAmount.compareTo(BigDecimal.ZERO)<0){
            return new ResponseEntity<>("Not Acceptable",HttpStatus.NOT_ACCEPTABLE);
        }

        Long fees = Long.valueOf(transferDTO.getFees());

        if (fees<=0 || fees >100){
            return new ResponseEntity<>("Not Acceptable",HttpStatus.NOT_ACCEPTABLE);
        }

        BigDecimal feesAmount = transferAmount.multiply(BigDecimal.valueOf(fees)).divide(BigDecimal.valueOf(100));

        BigDecimal transactionAmount = transferAmount.add(feesAmount);

        if (transactionAmount.compareTo(sender.getBalance())>0){
            return new ResponseEntity<>("Not Acceptable",HttpStatus.NOT_ACCEPTABLE);
        }

        Transfer transfer = new Transfer();
        transfer.setSender(sender);
        transfer.setRecipient(recipient);
        transfer.setFees(fees);
        transfer.setFeesAmount(feesAmount);
        transfer.setTransactionAmount(transactionAmount);
        transfer.setTransferAmount(transferAmount);

        customerService.transfer(transfer);

        transferDTO = transfer.toTransferDTO();

        return new ResponseEntity<>(transferDTO,HttpStatus.OK);
    }
}
