package be.cardoeng.example.configurations;

import be.cardoeng.example.entities.Member;
import be.cardoeng.example.enums.MemberStatus;
import be.cardoeng.example.repositories.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(MemberRepository repository) {
        return args -> {
            repository.save(new Member(1, "Guillaume", "Cardoen", "Guillaume.CARDOEN@umons.ac.be",
                    MemberStatus.Assistant, new SimpleDateFormat("dd/MM/yyyy").parse("15/09/2023"), null));
        };
    }

}
