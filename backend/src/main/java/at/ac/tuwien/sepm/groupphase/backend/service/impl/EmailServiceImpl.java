package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class EmailServiceImpl {

    @Autowired
    private JavaMailSender javaMailSender;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void sendPasswordResetMail(String recipient, String link, String token) {
        LOGGER.trace("sendPasswordResetMail({},{},{})", recipient, link, token);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Reset your Password");
        message.setText("Dear Customer.\n Here is the password reset link you requested:\n" + link + "\nClick on the link to reset your password.");

        javaMailSender.send(message);
    }
}
