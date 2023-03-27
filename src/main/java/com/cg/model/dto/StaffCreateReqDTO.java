package com.cg.model.dto;


import com.cg.model.Staff;
import com.cg.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StaffCreateReqDTO {

    private Long id;

    private String username;

    private String password;

    private RoleDTO role;
    private String fullName;
    private String phone;

    public Staff toStaff(User user) {
        return new Staff()
                .setId(id)
                .setFullName(fullName)
                .setPhone(phone)
                .setUser(user)
                ;
    }
}
