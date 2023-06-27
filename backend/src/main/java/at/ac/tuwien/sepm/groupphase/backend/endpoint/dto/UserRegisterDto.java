package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record UserRegisterDto(
    Long id,

    @Column(unique = true, nullable = false)
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

    @Pattern(regexp = "[ÄÖÜäöüßA-Za-z0-9\\s/-]*")
    @Column(nullable = false)
    String address,

    @Column(nullable = false)
    @Positive
    Long areaCode,

    @Pattern(regexp = "[ÄÖÜA-Zäöüa-z]*")
    @Column(nullable = false)
    String cityName,

    @Column(nullable = false)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    String password
) {
}

