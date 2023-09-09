package be.cardoeng.example.configurations;

import be.cardoeng.example.entities.Member;
import be.cardoeng.example.enums.MemberStatus;
import be.cardoeng.example.repositories.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * <p class="en">A simple configuration to initialize the database with some data.</p>
 * <p class="fr">Une simple configuration pour initialiser la base de données avec quelques données.</p>
 */
@Configuration
public class LoadDatabase {

    /**
     * <p class="en">Initialize the database with some data.</p>
     * <p class="fr">Initialise la base de données avec quelques données.</p>
     * @param repository <p class="en">The repository to use to manage the members.</p>
     * <p class="fr">Le dépôt à utiliser pour gérer les membres.</p>
     * @return <p class="en">A {@link CommandLineRunner} to initialize the database.</p>
     * <p class="fr">Un {@link CommandLineRunner} pour initialiser la base de données.</p>
     */
    @Bean
    CommandLineRunner initDatabase(MemberRepository repository) {
        return args -> {
            repository.save(new Member(1, "Tom", "Mens", "Tom.MENS@umons.ac.be",
                    MemberStatus.Assistant, new SimpleDateFormat("dd/MM/yyyy").parse("01/10/2003"), null));
            repository.save(new Member(2, "Guillaume", "Cardoen", "Guillaume.CARDOEN@umons.ac.be",
                    MemberStatus.Assistant, new SimpleDateFormat("dd/MM/yyyy").parse("15/09/2023"), null));
            repository.save(new Member(3, "Valentin", "Dusollier", "Valentin.Dusollier@umons.ac.be",
                    MemberStatus.Assistant, new SimpleDateFormat("dd/MM/yyyy").parse("15/09/2023"), null));
            repository.save(new Member(4, "Sébastien", "Bonte", "Sebastien.BONTE@umons.ac.be",
                    MemberStatus.Assistant, new SimpleDateFormat("dd/MM/yyyy").parse("15/09/2019"), null));
        };
    }

}
