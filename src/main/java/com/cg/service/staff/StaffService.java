package com.cg.service.staff;

import com.cg.model.Staff;
import com.cg.model.User;
import com.cg.model.dto.StaffCreateReqDTO;
import com.cg.model.dto.StaffInfoDTO;
import com.cg.repository.StaffRepository;
import com.cg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class StaffService implements IStaffService{

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;


    @Override
    public void create(StaffCreateReqDTO staffCreateReqDTO) {
        User user = new User();

        user.setId(null);
        user.setUsername(staffCreateReqDTO.getUsername());
        user.setRole(staffCreateReqDTO.getRole().toRole());
        user.setPassword(passwordEncoder.encode(staffCreateReqDTO.getPassword()));

        userRepository.save(user);

        Staff staff = staffCreateReqDTO.toStaff(user);

        staffRepository.save(staff);

    }

    @Override
    public Optional<StaffInfoDTO> getStaffInfoByUsername(String username) {
        return staffRepository.getStaffInfoByUsername(username);
    }

    @Override
    public List<Staff> findAll() {
        return null;
    }

    @Override
    public Optional<Staff> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Boolean existById(Long id) {
        return null;
    }

    @Override
    public Staff save(Staff staff) {
        return null;
    }

    @Override
    public void delete(Staff staff) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
