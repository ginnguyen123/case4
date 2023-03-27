package com.cg.service.customer;

import com.cg.exception.DataInputException;
import com.cg.model.*;
import com.cg.model.dto.CustomerCreateAvatarResDTO;
import com.cg.model.dto.CustomerDTO;
import com.cg.model.dto.CustomerResDTO;
import com.cg.model.dto.CustomerUpdateAvatarResDTO;
import com.cg.repository.*;
import com.cg.service.uploadMedia.UploadService;
import com.cg.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Transactional
public class CustomerService implements ICustomerService{

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private DepositRepository depositRepository;
    @Autowired
    private WithdrawRepository withdrawRepository;
    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private LocationRegionRepository locationRegionRepository;
    @Autowired
    private CustomerAvatarRepository customerAvatarRepository;
    @Autowired
    private UploadService uploadService;

    @Autowired
    private UploadUtils uploadUtils;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<CustomerResDTO> findCustomerResDTOById(Long id) {
        return customerRepository.findCustomerResDTOById(id);
    }

    @Override
    public List<CustomerDTO> findAllCustomerDTO() {
        return customerRepository.findAllCustomerDTO();
    }

    @Override
    public List<CustomerDTO> findAllCustomerDTOIsFalse() {
        return customerRepository.findAllCustomerDTOIsFalse();
    }

    @Override
    public List<CustomerResDTO> findAllByDeletedIsFalse() {
        return customerRepository.findAllByDeletedIsFalse();
    }

    @Override
    public List<CustomerDTO> findAllRecipients(Long senderId) {
        List<CustomerDTO> recipients = new ArrayList<>();
        List<CustomerDTO> getAllCustomer = findAllCustomerDTOIsFalse();
        for (CustomerDTO customerDTO : getAllCustomer){
            if (customerDTO.getId() != senderId){
                recipients.add(customerDTO);
            }
        }
        return recipients;
    }

    @Override
    public CustomerCreateAvatarResDTO createWithAvatar(Customer customer, MultipartFile avatarFile) {
        LocationRegion locationRegion = customer.getLocationRegion();
        locationRegionRepository.save(locationRegion);

        customer.setLocationRegion(locationRegion);
        customerRepository.save(customer);

        CustomerAvatar customerAvatar = new CustomerAvatar();
        customerAvatar.setCustomer(customer);

        customerAvatarRepository.save(customerAvatar);

        uploadAndSaveCustomerAvatar(avatarFile, customerAvatar);

        return new CustomerCreateAvatarResDTO(customer, locationRegion, customerAvatar.toCustomerAvatarDTO());
    }

    @Override
    public CustomerUpdateAvatarResDTO update(Customer customer) {
        LocationRegion locationRegion = customer.getLocationRegion();
        locationRegionRepository.save(locationRegion);

        customer.setLocationRegion(locationRegion);
        customerRepository.save(customer);

        CustomerAvatar customerAvatar = customerAvatarRepository.findByCustomer(customer).get();

        return new CustomerUpdateAvatarResDTO(customer, locationRegion, customerAvatar.toCustomerAvatarDTO());
    }

    @Override
    public CustomerUpdateAvatarResDTO updateWithAvatar(Customer customer, MultipartFile avatarFile) throws IOException {
        LocationRegion locationRegion = customer.getLocationRegion();
        locationRegionRepository.save(locationRegion);

        customer.setLocationRegion(locationRegion);

        customerRepository.save(customer);

        Optional<CustomerAvatar> customerAvatarOptional = customerAvatarRepository.findByCustomer(customer);

        CustomerAvatar customerAvatar = new CustomerAvatar();

        if (!customerAvatarOptional.isPresent()) {
            customerAvatar.setCustomer(customer);

            customerAvatarRepository.save(customerAvatar);

            uploadAndSaveCustomerAvatar(avatarFile, customerAvatar);
        }
        else {
            customerAvatar = customerAvatarOptional.get();
            uploadService.destroyImage(customerAvatar.getCloudId(), uploadUtils.buildCustomerImageUploadParams(customerAvatar));
            uploadAndSaveCustomerAvatar(avatarFile, customerAvatar);
        }

        return new CustomerUpdateAvatarResDTO(customer, locationRegion, customerAvatar.toCustomerAvatarDTO());
    }

    private void uploadAndSaveCustomerAvatar(MultipartFile file, CustomerAvatar customerAvatar) {

        try {
            Map uploadResult = uploadService.uploadImage(file, uploadUtils.buildImageUploadParams(customerAvatar));
            String fileUrl = (String) uploadResult.get("secure_url");
            String fileFormat = (String) uploadResult.get("format");

            customerAvatar.setFileName(customerAvatar.getId() + "." + fileFormat);
            customerAvatar.setFileUrl(fileUrl);
            customerAvatar.setFileFolder(uploadUtils.IMAGE_UPLOAD_FOLDER);
            customerAvatar.setCloudId(customerAvatar.getFileFolder() + "/" + customerAvatar.getId());
            customerAvatarRepository.save(customerAvatar);

        } catch (IOException e) {
            e.printStackTrace();
            throw new DataInputException("Upload hình ảnh thất bại");
        }
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Boolean existById(Long id) {
        return customerRepository.existsById(id);
    }

    @Override
    public Boolean existsByEmailEquals(String email) {
        return customerRepository.existsByEmailEquals(email);
    }

    @Override
    public Customer save(Customer customer) {
        locationRegionRepository.save(customer.getLocationRegion());

        customer.setLocationRegion(customer.getLocationRegion());
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }

    @Override
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public List<Customer> findAllByIdNot(Long id) {
        return customerRepository.findAllByIdNot(id);
    }

    @Override
    public Deposit deposit(Deposit deposit) {
        depositRepository.save(deposit);
        customerRepository.incremenBalance(deposit.getTransactionAmount(),deposit.getCustomer());
        return deposit;
    }

    @Override
    public void incremenBalance(BigDecimal transactionAmount, Customer customer) {
        customerRepository.incremenBalance(transactionAmount,customer);
    }

    @Override
    public Withdraw withdraw(Withdraw withdraw) {
        withdrawRepository.save(withdraw);
        customerRepository.decreaseBalance(withdraw.getTransAmount(),withdraw.getCustomer());
        return withdraw;
    }

    @Override
    public void decreaseBalance(BigDecimal transactionAmount, Customer customer) {
        customerRepository.decreaseBalance(transactionAmount,customer);
    }

    @Override
    public Transfer transfer(Transfer transfer) {
        transferRepository.save(transfer);
        customerRepository.incremenBalance(transfer.getTransferAmount(),transfer.getRecipient());
        customerRepository.decreaseBalance(transfer.getTransactionAmount(),transfer.getSender());
        return transfer;
    }
}
