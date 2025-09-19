package demo.game;

import demo.player.Player;
import demo.player.PlayerRepository;
import demo.question.*;
import demo.api.OpenTdbClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class GameService {
    private final OpenTdbClient openTdbClient;
    private final GameResultRepository gameResultRepo;
    private final PlayerRepository playerRepo;
    private final QuestionRepository questionRepo;
    private final GameQuestionRepository gameQuestionRepo;
//    @Value("${trivia.game.stop-on-first-wrong}")
//    private boolean stopOnFirstWrong;

    public GameSession startNewGame(String playerName, Difficulty difficulty) {
        // Check if player exists
        Player player = playerRepo.findByUsername(playerName.trim());
        if (player == null) {
            player = Player.builder()
                    .username(playerName.trim())
                    .registeredAt(Instant.now())
                    .build();
            player = playerRepo.save(player);
        }

        // Fetch from API
        List<QuestionData> fetched = openTdbClient.fetchQuestions(difficulty);

        // Map to DTO
        List<QuestionDTO> questions = fetched.stream()
                .map(qData -> new QuestionDTO(
                        qData.getQuestionText(),
                        qData.getCorrectAnswer(),
                        new ArrayList<>(qData.getIncorrectAnswers())
                ))
                .toList();

        return GameSession.builder()
                .playerId(player.getId())
                .playerName(player.getUsername())
                .difficulty(difficulty)
                .questions(questions)
                .index(0)
                .correctCount(0)
                .startedAt(Instant.now())
                .finished(false)
                .build();
    }

//    public void answer(GameSession s, String selectedOptionId) {
//        if (s.isFinished()) return;
//
//        QuestionDTO q = s.getQuestions().get(s.getIndex());
//        boolean correct = Objects.equals(selectedOptionId, q.getCorrectAnswer());
//
//        if (correct) {
//            s.setCorrectCount(s.getCorrectCount() + 1);
//        }
//
//        // Store the answer in session's answer history
//        s.getAnswers().put(q, selectedOptionId);
//
//        if (!correct && stopOnFirstWrong) {
//            finishAndPersist(s);
//        } else {
//            int next = s.getIndex() + 1;
//            if (next >= s.getQuestions().size()) {
//                finishAndPersist(s);
//            } else {
//                s.setIndex(next);
//            }
//        }
//    }

    public void finishAndPersist(GameSession s) {
        s.setFinished(true);

        Player player = playerRepo.findById(s.getPlayerId())
                .orElseThrow(() -> new IllegalStateException("Player not found"));

        GameResult result = GameResult.builder()
                .player(player)
                .difficulty(s.getDifficulty())
                .totalQuestions(s.getQuestions().size())
                .correctAnswers(s.getCorrectCount())
                .durationMs(Duration.between(s.getStartedAt(), Instant.now()).toMillis())
                .finishedAt(Instant.now())
                .build();
        result = gameResultRepo.save(result);

        for (QuestionDTO dto : s.getQuestions()) {
            // Find the DB entity
            Question entity = questionRepo.findByQuestionText(dto.getQuestionText())
                    .orElseGet(() -> {
                        // Save if not in DB
                        Question newQ = Question.builder()
                                .category("Unknown")
                                .difficulty(s.getDifficulty())
                                .questionText(dto.getQuestionText())
                                .correctAnswer(dto.getCorrectAnswer())
                                .incorrectAnswers(new ArrayList<>(dto.getIncorrectAnswers()))
                                .build();
                        return questionRepo.save(newQ);
                    });

            String chosen = s.getAnswers().get(dto);
            boolean correct = Objects.equals(chosen, dto.getCorrectAnswer());

            GameQuestion gq = GameQuestion.builder()
                    .id(new GameQuestionId(result.getId(), entity.getId())) // ✅ Set composite key
                    .gameResult(result)
                    .question(entity)
                    .chosenAnswer(chosen)
                    .isCorrect(correct)
                    .build();
            gameQuestionRepo.save(gq);

        }
    }
}