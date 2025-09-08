package demo.game;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter@Setter@Builder
public class GameSession {
    private String playerName;
    private Difficulty difficulty;
    private List<Question> questions;
    private int index;
    private int correctCount;
    private Instant startedAt;
    private boolean finished;

    @Getter @Setter @Builder
    public static class Question {
        private String text;
        private List<AnswerOption> options;
        private String correctOptionId;
    }
    @Getter @Setter @Builder
    public static class AnswerOption {
        private String id;
        private String text;
    }

}
