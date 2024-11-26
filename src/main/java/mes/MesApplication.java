package mes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@SpringBootApplication
@EnableJpaAuditing

@EntityScan(basePackages = {"mes.domain.entity"})
public class MesApplication {
	public static void main(String[] args) {
		SpringApplication.run(MesApplication.class, args);
	}
}
