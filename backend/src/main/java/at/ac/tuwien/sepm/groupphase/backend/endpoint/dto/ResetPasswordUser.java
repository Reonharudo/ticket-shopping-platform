package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public record ResetPasswordUser(
    String email,

    String newPassword,

    String token) {

}
