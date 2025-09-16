package demo.game;

import demo.question.Question;
import demo.question.QuestionDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class GameSession {
    private Long playerId;
    private String playerName;
    private Difficulty difficulty;
    private List<QuestionDTO> questions;
    private int index;
    private int correctCount;
    private Instant startedAt;
    private boolean finished;

    // Map to track chosen answers for each question
    @Builder.Default
    private Map<QuestionDTO, String> answers = new HashMap<>();
}


