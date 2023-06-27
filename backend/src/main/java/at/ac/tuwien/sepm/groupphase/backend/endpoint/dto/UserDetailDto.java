package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record UserDetailDto(
    Long id,

    Boolean admin,

    @Column(nullable = false, unique = true)
    String email,
    @Pattern(regexp = "[ÄÖÜA-Zäöüa-z]*", message = "First name must contain only letters")
    @Column(nullable = false)
    String firstName,
    @Pattern(regexp = "[ÄÖÜA-Zäöüa-z]*", message = "Last name must contain only letters")
    @Column(nullable = false)
    String lastName,

    @Column(nullable = false)
    @Past(message = "Birthdate must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthdate,

    @Pattern(regexp = "[ÄÖÜäöüßA-Za-z0-9\\s/-]*")
    @Column(nullable = false)
    String address,

    @Positive(message = "Area Code must be a positive number")
    @Column(nullable = false)
    Long areaCode,

    @Pattern(regexp = "[ÄÖÜA-Zäöüa-z]*", message = "City name must contain only letters")
    @Column(nullable = false)
    String cityName,

    @Column(nullable = false)
    String password,

    @Column(nullable = false)
    Boolean isLocked
) {
}
