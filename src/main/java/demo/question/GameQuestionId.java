package demo.question;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class GameQuestionId implements Serializable {

    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "question_id")
    private Long questionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameQuestionId that)) return false;
        return Objects.equals(gameId, that.gameId) &&
                Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, questionId);
    }
}
