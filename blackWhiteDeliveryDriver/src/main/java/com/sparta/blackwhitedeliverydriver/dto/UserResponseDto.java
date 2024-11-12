package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String username;
    private String email;
    private String phoneNumber;
    private boolean publicProfile;
    private UserRoleEnum role;
    private String imgUrl;

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.publicProfile = user.isPublicProfile();
        this.role = user.getRole();
        this.imgUrl = user.getImageUrl();
    }
}
