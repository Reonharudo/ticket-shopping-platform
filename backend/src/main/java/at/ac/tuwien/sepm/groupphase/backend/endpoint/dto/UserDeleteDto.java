package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import jakarta.persistence.Column;

public record UserDeleteDto(
    Long id,
    @Column(nullable = false, unique = true)
    String email,
    @Column(nullable = false)
    String password
) {
}
