package ShutterMats.Backend.service;

import ShutterMats.Backend.entity.ContactMessage;

public interface EmailService {

    /**
     * Notifies the admin that a new contact message has arrived.
     * Must never throw: if sending fails, it should be logged and the
     * calling flow (saving the message) must keep working regardless.
     */
    void sendContactMessageNotification(ContactMessage contactMessage);
}
