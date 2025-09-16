package demo.question;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuestionDTO {
    private String questionText;
    private String correctAnswer;
    private List<String> incorrectAnswers;
}
