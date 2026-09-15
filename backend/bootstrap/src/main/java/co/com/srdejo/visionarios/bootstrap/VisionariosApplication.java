package co.com.srdejo.visionarios.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "co.com.srdejo.visionarios")
@EntityScan(basePackages = "co.com.srdejo.visionarios")
@EnableJpaRepositories(basePackages = "co.com.srdejo.visionarios")
@EnableScheduling
public class VisionariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(VisionariosApplication.class, args);
    }
}
