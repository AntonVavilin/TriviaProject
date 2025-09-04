package demo.game;

import demo.game.Difficulty;
import demo.game.GameResult;
import demo.game.GameResultRepository;
import demo.game.GameSession;
import demo.web.OpenTdbClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

// START GENAI
@Service
@RequiredArgsConstructor
public class GameService {
    private final OpenTdbClient openTdbClient;
    private final GameResultRepository repo;
    @Value("${trivia.game.stop-on-first-wrong}") private boolean stopOnFirstWrong;

    public GameSession startNewGame(String playerName, Difficulty difficulty) {
        var session = GameSession.builder()
                .playerName(playerName.trim())
                .difficulty(difficulty)
                .questions(openTdbClient.fetchQuestions(difficulty))
                .index(0)
                .correctCount(0)
                .startedAt(Instant.now())
                .finished(false)
                .build();
        return session;
    }

    public void answer(GameSession s, String selectedOptionId) {
        if (s.isFinished()) return;
        var q = s.getQuestions().get(s.getIndex());
        boolean correct = Objects.equals(selectedOptionId, q.getCorrectOptionId());
        if (correct) s.setCorrectCount(s.getCorrectCount() + 1);
        if (!correct && stopOnFirstWrong) {
            finishAndPersist(s);
        } else {
            int next = s.getIndex() + 1;
            if (next >= s.getQuestions().size()) {
                finishAndPersist(s);
            } else {
                s.setIndex(next);
            }
        }
    }

    private void finishAndPersist(GameSession s) {
        s.setFinished(true);
        var result = GameResult.builder()
                .playerName(s.getPlayerName())
                .difficulty(s.getDifficulty())
                .totalQuestions(s.getQuestions().size())
                .correctAnswers(s.getCorrectCount())
                .durationMs(Duration.between(s.getStartedAt(), Instant.now()).toMillis())
                .finishedAt(Instant.now())
                .build();
        repo.save(result);
    }
}
