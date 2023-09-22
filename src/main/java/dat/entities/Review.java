package dat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder

@Entity
public class Review
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String title;
    Integer rating;
   // @Column(length = 5000)
    @Column(columnDefinition = "TEXT")
    String content;
    LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    Registrant registrant;
}
