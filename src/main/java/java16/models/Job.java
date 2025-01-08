package java16.models;

import java16.enums.Description;
import java16.enums.Position;
import java16.enums.Profession;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Job {
    private Long id;
    private Position position;
    private Profession profession;
    private Description description;
    private int experience;

    public Job(Position position, Profession profession, Description description, int experience) {
        this.position = position;
        this.profession = profession;
        this.description = description;
        this.experience = experience;
    }
}
