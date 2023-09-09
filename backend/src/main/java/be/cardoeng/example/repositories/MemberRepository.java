package be.cardoeng.example.repositories;

import be.cardoeng.example.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p class="en">A simple repository to manage the members. It communicates with the database.</p>
 * <p class="fr">Un simple dépôt pour gérer les membres. Il communique avec la base de données.</p>
 */
public interface MemberRepository extends JpaRepository<Member, Integer> {
}
