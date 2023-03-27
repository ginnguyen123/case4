package com.cg.model.dto;


import com.cg.model.LocationRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerResDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private BigDecimal balance;

    private String avatarId;
    private String fileFolder;
    private String fileName;
    private String fileUrl;

    @Valid
    private LocationRegionDTO locationRegion;


    public CustomerResDTO(Long id, String fullName, String email, String phone, BigDecimal balance, String avatarId, String fileFolder, String fileName, String fileUrl, LocationRegion locationRegion) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.balance = balance;
        this.avatarId = avatarId;
        this.fileFolder = fileFolder;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.locationRegion = locationRegion.toLocationRegionDTO();
    }

    public CustomerDTO toCustomerDTO(CustomerAvatarDTO customerAvatar) {
        return new CustomerDTO()
                .setId(id)
                .setFullName(fullName)
                .setEmail(email)
                .setPhone(phone)
                .setBalance(balance)
                .setCustomerAvatar(customerAvatar)
                .setLocationRegion(locationRegion)
                ;
    }
}
