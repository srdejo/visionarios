package co.com.srdejo.visionarios.platform.webcommon.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MailConfig {

    @Bean
    public RestClient contactApiClient(@Value("${contact.api.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public MailSender mailSender(RestClient contactApiClient, @Value("${contact.mail.from-name}") String fromName) {
        return new ContactApiMailSender(contactApiClient, fromName);
    }
}
