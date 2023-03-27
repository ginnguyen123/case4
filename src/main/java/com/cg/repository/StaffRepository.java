package com.cg.repository;

import com.cg.model.Staff;
import com.cg.model.dto.StaffCreateReqDTO;
import com.cg.model.dto.StaffInfoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StaffRepository extends JpaRepository<Staff,Long> {


    @Query("SELECT NEW com.cg.model.dto.StaffInfoDTO (" +
            "st.fullName, " +
            "st.phone" +
            ") " +
            "FROM Staff AS st " +
            "JOIN User AS us " +
            "ON st.user = us " +
            "AND us.username = :username"
    )
    Optional<StaffInfoDTO> getStaffInfoByUsername(@Param("username") String username);

}
