package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserCreateDto(

    Long id,

    @Column(nullable = false, unique = true)
    String email,

    @Column(nullable = false)
    @Pattern(regexp = "[ÄÖÜA-Zäöüa-z]*", message = "First name must contain only letters")
    String firstName,

    @Column(nullable = false)
    @Pattern(regexp = "[ÄÖÜA-Zäöüa-z]*", message = "Last name must contain only letters")
    String lastName,

    @Column(nullable = false)
    @Past(message = "Birthdate must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthdate,

    @Column(nullable = false)
    String address,

    @Column(nullable = false)
    Long areaCode,

    @Column(nullable = false)
    String cityName,

    @Column(nullable = false)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    String password,

    @Column(nullable = false)
    Boolean admin,

    @Column(nullable = false)
    Boolean isLocked) {
}