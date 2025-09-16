package demo.question;

import demo.game.Difficulty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private Difficulty difficulty;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "question_incorrect_answers",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "incorrect_answer", nullable = false, length = 255)
    @Builder.Default
    private List<String> incorrectAnswers = new ArrayList<>();

    @OneToMany(mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<GameQuestion> gameQuestions = new ArrayList<>();
}
