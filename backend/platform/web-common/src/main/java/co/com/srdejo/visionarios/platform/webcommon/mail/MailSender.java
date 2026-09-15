package co.com.srdejo.visionarios.platform.webcommon.mail;

/** Envia los correos transaccionales de la app (diseños en designdownloaded/DESIGN_SPEC.md). */
public interface MailSender {

    void sendPasswordReset(String toEmail, String recipientName, String resetLink);

    void sendAdminInvite(String toEmail, String inviterName, String inviteLink);

    void sendEventInvitation(String toEmail, EventInvitationMail data);
}
