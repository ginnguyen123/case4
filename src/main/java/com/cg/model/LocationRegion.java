package com.cg.model;

import com.cg.model.dto.LocationRegionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "location_region")
@Accessors(chain = true)
public class LocationRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provinceId;

    private String provinceName;

    private String districtId;

    private String districtName;

    private String wardId;

    private String wardName;

    private String address;


    @OneToMany
    private List<Customer> customers;

//    public LocationRegionDTO toLocationRegionDTO() {
//        LocationRegionDTO locationRegionDTO = new LocationRegionDTO();
//        locationRegionDTO.setId(id);
//        locationRegionDTO.setProvinceId(provinceId);
//        locationRegionDTO.setProvinceName(provinceName);
//        locationRegionDTO.setDistrictId(districtId);
//        locationRegionDTO.setDistrictName(districtName);
//        locationRegionDTO.setWardId(wardId);
//        locationRegionDTO.setWardName(wardName);
//        locationRegionDTO.setAddress(address);
//
//        return locationRegionDTO;
//    }

    public LocationRegionDTO toLocationRegionDTO() {
        return new LocationRegionDTO()
                .setId(id)
                .setProvinceId(provinceId)
                .setProvinceName(provinceName)
                .setDistrictId(districtId)
                .setDistrictName(districtName)
                .setWardId(wardId)
                .setWardName(wardName)
                .setAddress(address)
                ;
    }

}
