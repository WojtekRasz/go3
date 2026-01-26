package lista4.backend;

import lista4.dbRepositories.GameRepository;
import lista4.dbRepositories.MoveRepository;
import lista4.gameLogic.GameManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = "lista4") // Klucz do sukcesu!
@EnableJpaRepositories(basePackages = "lista4.dbRepositories")
@EntityScan(basePackages = "lista4.dbModel")
public class ServerDB {
    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = SpringApplication.run(ServerDB.class, args);

        GameRepository gameRepo = context.getBean(GameRepository.class);
        MoveRepository moveRepo = context.getBean(MoveRepository.class);

        GameManager.getInstance().setRepositories(gameRepo, moveRepo);

        Server server = context.getBean(Server.class);

        server.start();
    }
}
