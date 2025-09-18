package demo.question;

import demo.game.GameResult;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameQuestion {

    @EmbeddedId
    private GameQuestionId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("gameId")
    @JoinColumn(name = "game_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_game_question_game_result"))
    private GameResult gameResult;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("questionId")
    @JoinColumn(name = "question_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_game_question_question"))
    private Question question;

    @Column(name = "chosen_answer", length = 255)
    private String chosenAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;
}
