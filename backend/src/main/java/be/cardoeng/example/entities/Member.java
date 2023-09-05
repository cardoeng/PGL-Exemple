package be.cardoeng.example.entities;

import be.cardoeng.example.enums.MemberStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Member {

    @Id private int id;
    private String firstName;
    private String lastName;
    private String email;
    private MemberStatus status;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date beginDate;
    @Nullable
     @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;

}
