package com.cg.model.dto;


import com.cg.model.Customer;
import com.cg.model.LocationRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerCreateAvatarResDTO {

    private Long id;

    private String fullName;
    private String email;
    private String phone;

    private LocationRegionDTO locationRegion;

    private CustomerAvatarDTO customerAvatar;

    public CustomerCreateAvatarResDTO(Customer customer, LocationRegion locationRegion, CustomerAvatarDTO customerAvatarDTO) {
        this.id = customer.getId();
        this.fullName = customer.getFullName();
        this.email = customer.getEmail();
        this.phone = customer.getPhone();
        this.locationRegion = locationRegion.toLocationRegionDTO();
        this.customerAvatar = customerAvatarDTO;
    }
}
