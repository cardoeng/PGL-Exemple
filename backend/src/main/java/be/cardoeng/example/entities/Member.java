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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Member {

    @Id private int id;
    private String firstName;
    private String lastName;
    private String email;
    private MemberStatus status;
    private Date beginDate;
    @Nullable
    private Date endDate;

    

}
