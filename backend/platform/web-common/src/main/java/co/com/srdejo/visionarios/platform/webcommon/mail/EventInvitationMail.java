package co.com.srdejo.visionarios.platform.webcommon.mail;

/** Variables ya formateadas para la plantilla {@code event-invitation.html}. */
public record EventInvitationMail(
        String recipientName,
        String eventTitle,
        String dateTimeLine,
        String placeLine,
        String description,
        String day,
        String month,
        String audienceLine,
        String confirmedLine,
        String eventLink,
        String calendarLink,
        String unsubscribeLink
) {
}
