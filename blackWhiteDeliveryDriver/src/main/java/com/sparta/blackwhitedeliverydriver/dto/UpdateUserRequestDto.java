package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UpdateUserRequestDto {

    @Size(min = 8, max = 15)
    @Pattern(regexp = "^[a-zA-Z\\d!@#$%^&*()_+\\-=]*$")
    private String password;

    @Email
    private String email;

    @Size(min = 11, max = 11)
    @Pattern(regexp = "^[0-9]{11}$")
    private String phoneNumber;

    private boolean publicProfile;

    private String imgUrl;
}

