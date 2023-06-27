package at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler;

public record ErrorResponse(
    int status,
    String error,
    String message,
    String path
) {
}
