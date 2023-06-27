package at.ac.tuwien.sepm.groupphase.backend.service;

public interface PasswordResetService {


    /**
     * Sends a reset mail to the given email-address.
     *
     * @param email the email-address which receives the mail
     */
    void initiatePasswordReset(String email);


}
