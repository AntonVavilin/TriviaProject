package demo.web;

import demo.game.Difficulty;
import demo.game.GameSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class OpenTdbClient {
    private final WebClient client;
    private final int numQuestions;
    private final int categoryId;

    public OpenTdbClient(WebClient.Builder builder,
                         @Value("${trivia.game.num-questions}") int numQuestions,
                         @Value("${trivia.game.category-id}") int categoryId,
                         @Value("${trivia.opentdb.base-url}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
        this.numQuestions = numQuestions;
        this.categoryId = categoryId;
    }

    public List<GameSession.Question> fetchQuestions(Difficulty difficulty) {
        String diff = difficulty.name().toLowerCase();
        OpenTdbResponse resp = client.get()
                .uri(uri -> uri.path("/api.php")
                        .queryParam("amount", numQuestions)
                        .queryParam("category", categoryId)
                        .queryParam("type", "multiple")
                        .queryParam("difficulty", diff)
                        .build())
                .retrieve()
                .bodyToMono(OpenTdbResponse.class)
                .block();

        if (resp == null || resp.results == null || resp.results.isEmpty()) {
            throw new IllegalStateException("No questions available from OpenTDB");
        }
        return mapToQuestions(resp.results);
    }

    private List<GameSession.Question> mapToQuestions(List<OpenTdbQuestion> apiQs) {
        return apiQs.stream().map(q -> {
            String text = HtmlUtils.htmlUnescape(q.question);
            List<String> all = new ArrayList<>(q.incorrect_answers);
            all.add(q.correct_answer);
            Collections.shuffle(all);
            List<GameSession.AnswerOption> opts = new ArrayList<>();
            String correctId = null;
            for (String a : all) {
                String id = UUID.randomUUID().toString();
                String decoded = HtmlUtils.htmlUnescape(a);
                opts.add(GameSession.AnswerOption.builder().id(id).text(decoded).build());
                if (a.equals(q.correct_answer)) correctId = id;
            }
            return GameSession.Question.builder()
                    .text(text)
                    .options(opts)
                    .correctOptionId(correctId)
                    .build();
        }).toList();
    }

    @Data
    static class OpenTdbResponse {
        int response_code;
        List<OpenTdbQuestion> results;
    }

    @Data
    static class OpenTdbQuestion {
        String category;
        String type;
        String difficulty;
        String question;
        String correct_answer;
        List<String> incorrect_answers;
    }
}
