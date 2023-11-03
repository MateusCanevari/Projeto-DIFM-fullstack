package br.com.projetodifm.services;

import br.com.projetodifm.exceptions.EmailSendException;
import br.com.projetodifm.util.ErrorMessages;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class AbstractEmailService implements EmailService{

    private static final Logger LOGGER = Logger.getLogger(AbstractEmailService.class.getName());

    @Autowired
    private JavaMailSender mailSender;

    @Value("${support.mail}")
    private String sender;

    @Override
    @Async
    public void sendEmailVerification(String to, String body) {
        try {
            LOGGER.info("Sending Email");

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(body, true);
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject("Check your email");

            mailSender.send(mimeMessage);

            LOGGER.info("Email successfully sent");
        } catch (MessagingException e){
            throw new EmailSendException(ErrorMessages.FAILED_TO_SEND_EMAIL);
        }
    }
}
