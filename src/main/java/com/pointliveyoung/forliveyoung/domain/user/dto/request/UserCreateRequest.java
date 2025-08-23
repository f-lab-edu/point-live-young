package com.pointliveyoung.forliveyoung.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UserCreateRequest {

    @NotBlank
    @Length(min = 2, max = 50)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 8, max = 100)
    private String password;

    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
}
