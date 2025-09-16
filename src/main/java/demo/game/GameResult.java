package demo.game;

import demo.player.Player;
import demo.question.GameQuestion;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Difficulty difficulty;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Column(name = "correct_answers", nullable = false)
    private int correctAnswers;

    @Column(name = "duration_ms", nullable = false)
    private long durationMs;

    @Column(name = "finished_at", nullable = false)
    private Instant finishedAt;

    @OneToMany(mappedBy = "gameResult",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<GameQuestion> gameQuestions = new ArrayList<>();
}
