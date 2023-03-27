package com.cg.service.staff;

import com.cg.model.Staff;
import com.cg.model.dto.StaffCreateReqDTO;
import com.cg.model.dto.StaffInfoDTO;
import com.cg.service.IGeneralService;

import java.util.Optional;

public interface IStaffService extends IGeneralService<Staff> {

    void create(StaffCreateReqDTO staffCreateReqDTO);

    Optional<StaffInfoDTO> getStaffInfoByUsername(String username);
}
