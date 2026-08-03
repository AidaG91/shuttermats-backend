package ShutterMats.Backend.service;

import ShutterMats.Backend.entity.ContactMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String adminNotificationEmail;
    private final boolean notificationsEnabled;

    public EmailServiceImpl(JavaMailSender mailSender,
                             @Value("${app.mail.from}") String fromAddress,
                             @Value("${app.notifications.admin-email}") String adminNotificationEmail,
                             @Value("${app.notifications.enabled}") boolean notificationsEnabled) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.adminNotificationEmail = adminNotificationEmail;
        this.notificationsEnabled = notificationsEnabled;
    }

    @Override
    public void sendContactMessageNotification(ContactMessage contactMessage) {
        if (!notificationsEnabled) {
            return;
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromAddress);
            mail.setTo(adminNotificationEmail);
            mail.setReplyTo(contactMessage.getEmail());
            mail.setSubject("Nuevo mensaje de contacto: " + contactMessage.getSubject());
            mail.setText(
                    """
                    Has recibido un nuevo mensaje de contacto en ShutterMats.

                    Nombre: %s
                    Email: %s
                    Telefono: %s
                    Asunto: %s

                    Mensaje:
                    %s

                    Gestiona la respuesta desde el panel de admin, o responde \
                    directamente a %s.
                    """
                            .formatted(
                                    contactMessage.getName(),
                                    contactMessage.getEmail(),
                                    contactMessage.getPhone() != null ? contactMessage.getPhone() : "-",
                                    contactMessage.getSubject(),
                                    contactMessage.getMessage(),
                                    contactMessage.getEmail()));

            mailSender.send(mail);
        } catch (MailException ex) {
            // The message is already saved in the DB: an SMTP failure must
            // not break the contact form submit for the end user.
            log.warn("Failed to send the contact notification email: {}", ex.getMessage());
        }
    }
}
