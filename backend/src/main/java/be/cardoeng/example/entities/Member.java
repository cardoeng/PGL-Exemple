package be.cardoeng.example.entities;

import be.cardoeng.example.enums.MemberStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

/**
 * <p class="en">A simple entity to represent a member.</p>
 * <p class="fr">Une simple entité pour représenter un membre.</p>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Member {

    /**
     * <p class="en">The id of the member. It is unique</p>
     * <p class="fr">L'id du membre. Il est unique.</p>
     */
    @Id private int id;
    /**
     * <p class="en">The first name of the member.</p>
     * <p class="fr">Le prénom du membre.</p>
     */
    private String firstName;
    /**
     * <p class="en">The last name of the member.</p>
     * <p class="fr">Le nom de famille du membre.</p>
     */
    private String lastName;
    /**
     * <p class="en">The email of the member.</p>
     * <p class="fr">L'email du membre.</p>
     */
    private String email;
    /**
     * <p class="en">The status of the member. Represented by the enum {@link MemberStatus}.</p>
     * <p class="fr">Le statut du membre. Représenté par l'énumération {@link MemberStatus}.</p>
     */
    private MemberStatus status;
    /**
     * <p class="en">The date when the member joined the team.</p>
     * <p class="fr">La date à laquelle le membre a rejoint l'équipe.</p>
     */
    private Date beginDate;
    /**
     * <p class="en">The date when the member left the team.</p>
     * <p class="fr">La date à laquelle le membre a quitté l'équipe.</p>
     */
    @Nullable
    private Date endDate;

    

}
