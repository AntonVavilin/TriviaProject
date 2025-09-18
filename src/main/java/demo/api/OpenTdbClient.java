package demo.api;

import demo.game.Difficulty;
import demo.question.QuestionData;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

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

    public List<QuestionData> fetchQuestions(Difficulty difficulty) {
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
        return resp.results.stream()
                .map(q -> QuestionData.builder()
                        .category(HtmlUtils.htmlUnescape(q.category))
                        .difficulty(difficulty)
                        .questionText(HtmlUtils.htmlUnescape(q.question))
                        .correctAnswer(HtmlUtils.htmlUnescape(q.correct_answer))
                        .incorrectAnswers(q.incorrect_answers.stream()
                                .map(HtmlUtils::htmlUnescape)
                                .toList())
                        .build())
                .toList();
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
