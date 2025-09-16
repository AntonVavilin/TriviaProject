package demo.question;

import demo.game.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionData {
    private String category;
    private Difficulty difficulty;
    private String questionText;
    private String correctAnswer;
    private List<String> incorrectAnswers;

}
