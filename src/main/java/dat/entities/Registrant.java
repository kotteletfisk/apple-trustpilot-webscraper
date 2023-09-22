package dat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter

@Entity
public class Registrant
{
    @Id
    String name;
    Integer reviewAmount;
    String country;

    @OneToMany(mappedBy = "registrant", fetch = FetchType.EAGER)
    Set<Review> reviews = new HashSet<>();

    public Registrant(String name, Integer reviewAmount, String country)
    {
        this.name = name;
        this.reviewAmount = reviewAmount;
        this.country = country;
    }

    public void addReview(Review review)
    {
        if (review != null)
        {
            this.reviews.add(review);
            review.setRegistrant(this);
        }
    }
}
