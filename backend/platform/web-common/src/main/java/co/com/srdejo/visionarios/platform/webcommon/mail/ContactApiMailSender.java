package co.com.srdejo.visionarios.platform.webcommon.mail;

import co.com.srdejo.visionarios.platform.webcommon.BusinessRuleException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Envia los correos a traves del microservicio compartido {@code contact}
 * ({@code contact/src/server.js}, {@code POST /api/send}) en vez de hablar SMTP
 * directamente — mismo patron usado por micasachurch
 * (ContactApiPasswordResetMailSender). Ese endpoint solo acepta llamadas desde
 * loopback, asi que esto solo funciona cuando ambos servicios corren en el
 * mismo host (asi es en produccion).
 *
 * <p>Manda su propio {@code fromName} para que el correo se firme con el nombre
 * de este proyecto. La direccion de envio la resuelve contact, que es quien
 * conoce la cuenta configurada.
 */
public class ContactApiMailSender implements MailSender {

    private final RestClient contactApiClient;
    private final String fromName;
    private final MailTemplateRenderer templateRenderer = new MailTemplateRenderer();

    public ContactApiMailSender(RestClient contactApiClient, String fromName) {
        this.contactApiClient = contactApiClient;
        this.fromName = fromName;
    }

    @Override
    public void sendPasswordReset(String toEmail, String recipientName, String resetLink) {
        String html = templateRenderer.render("mail-templates/reset-password.html", Map.of(
                "recipientName", recipientName,
                "link", resetLink,
                "linkDisplay", stripProtocol(resetLink)
        ));
        send(toEmail, "Recupera tu contraseña · Red de Visionarios", html);
    }

    @Override
    public void sendEmailVerification(String toEmail, String recipientName, String verifyLink) {
        String html = templateRenderer.render("mail-templates/verify-email.html", Map.of(
                "recipientName", recipientName,
                "link", verifyLink,
                "linkDisplay", stripProtocol(verifyLink)
        ));
        send(toEmail, "Confirma tu correo · Red de Visionarios", html);
    }

    @Override
    public void sendAdminInvite(String toEmail, String inviterName, String inviteLink) {
        String html = templateRenderer.render("mail-templates/invite-admin.html", Map.of(
                "inviterName", inviterName,
                "link", inviteLink
        ));
        send(toEmail, "Te invitaron a administrar la Red de Visionarios", html);
    }

    @Override
    public void sendEventInvitation(String toEmail, EventInvitationMail data) {
        String html = templateRenderer.render("mail-templates/event-invitation.html", Map.ofEntries(
                Map.entry("preheader", data.dateTimeLine() + ". Confirma tu asistencia."),
                Map.entry("recipientName", data.recipientName()),
                Map.entry("eventTitle", data.eventTitle()),
                Map.entry("dateTimeLine", data.dateTimeLine()),
                Map.entry("placeLine", data.placeLine()),
                Map.entry("description", data.description()),
                Map.entry("day", data.day()),
                Map.entry("month", data.month()),
                Map.entry("audienceLine", data.audienceLine()),
                Map.entry("confirmedLine", data.confirmedLine()),
                Map.entry("eventLink", data.eventLink()),
                Map.entry("calendarLink", data.calendarLink()),
                Map.entry("unsubscribeLink", data.unsubscribeLink())
        ));
        send(toEmail, data.eventTitle(), html);
    }

    private void send(String toEmail, String subject, String html) {
        try {
            contactApiClient.post()
                    .uri("/api/send")
                    .body(new SendRequest(toEmail, subject, html, fromName))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new BusinessRuleException("mail.delivery_failed");
        }
    }

    private static String stripProtocol(String link) {
        return link.replaceFirst("^https?://", "");
    }

    private record SendRequest(String to, String subject, String html, String fromName) {
    }
}
