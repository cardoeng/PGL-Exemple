package be.cardoeng.example.repositories;

import be.cardoeng.example.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
}
