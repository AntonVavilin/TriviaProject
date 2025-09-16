
package demo.game;

import demo.form.AnswerForm;
import demo.form.NewGameForm;
import demo.question.QuestionDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;

@Controller
@RequestMapping("/trivia")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final GameResultRepository repo;

    /** Welcome page */
    @GetMapping
    public String welcome() {
        return "welcome";
    }

    /** Show new game form */
    @GetMapping("/new")
    public String newGame(Model model) {
        model.addAttribute("form", new NewGameForm());
        return "new-game";
    }

    /** Start a new game */
    @PostMapping("/new")
    public String startGame(@Valid @ModelAttribute("form") NewGameForm form,
                            BindingResult br,
                            HttpSession httpSession) {
        if (br.hasErrors()) return "new-game";

        GameSession session = gameService.startNewGame(form.getPlayerName(), form.getDifficulty());
        httpSession.setAttribute("GAME_SESSION", session);
        return "redirect:/trivia/play";
    }

    /** Show the current question */
    @GetMapping("/play")
    public String play(HttpSession httpSession, Model model) {
        GameSession s = (GameSession) httpSession.getAttribute("GAME_SESSION");
        if (s == null) return "redirect:/trivia";
        if (s.isFinished()) return "redirect:/trivia/finish";

        QuestionDTO q = s.getQuestions().get(s.getIndex());

        // Build shuffled options
        var options = new ArrayList<String>();
        options.add(q.getCorrectAnswer());
        options.addAll(q.getIncorrectAnswers());
        Collections.shuffle(options);

        model.addAttribute("question", q);
        model.addAttribute("options", options);
        model.addAttribute("index", s.getIndex() + 1);
        model.addAttribute("total", s.getQuestions().size());
        model.addAttribute("answerForm", new AnswerForm());

        return "play";
    }

    /** Process the submitted answer */
    @PostMapping("/answer")
    public String answer(@Valid @ModelAttribute AnswerForm answerForm,
                         BindingResult br,
                         HttpSession httpSession) {
        if (br.hasErrors()) return "redirect:/trivia/play";

        GameSession s = (GameSession) httpSession.getAttribute("GAME_SESSION");
        if (s == null) return "redirect:/trivia";

        QuestionDTO q = s.getQuestions().get(s.getIndex());
        String chosen = answerForm.getOptionId();

        // Save the answer
        s.getAnswers().put(q, chosen);

        // Check correctness
        if (chosen.equals(q.getCorrectAnswer())) {
            s.setCorrectCount(s.getCorrectCount() + 1);
        }

        // Move to next question or finish
        int next = s.getIndex() + 1;
        if (next >= s.getQuestions().size()) {
            gameService.finishAndPersist(s); // persist final result
        } else {
            s.setIndex(next);
        }

        httpSession.setAttribute("GAME_SESSION", s);
        return s.isFinished() ? "redirect:/trivia/finish" : "redirect:/trivia/play";
    }

    /** Finish screen */
    @GetMapping("/finish")
    public String finish(HttpSession httpSession, Model model) {
        GameSession s = (GameSession) httpSession.getAttribute("GAME_SESSION");
        if (s == null) return "redirect:/trivia";

        if (!s.isFinished()) {
            gameService.finishAndPersist(s);
        }

        model.addAttribute("playerName", s.getPlayerName());
        model.addAttribute("score", s.getCorrectCount());
        model.addAttribute("total", s.getQuestions().size());
        model.addAttribute("difficulty", s.getDifficulty());

        httpSession.removeAttribute("GAME_SESSION");
        return "finish";
    }

    /** Scoreboard with difficulty filter */
    @GetMapping("/scoreboard")
    public String scoreboard(@RequestParam(defaultValue = "EASY") Difficulty difficulty,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        Page<GameResult> results = repo
                .findByDifficultyOrderByCorrectAnswersDescDurationMsAscFinishedAtDesc(
                        difficulty, PageRequest.of(page, 20));

        model.addAttribute("results", results);
        model.addAttribute("difficulty", difficulty);
        return "scoreboard";
    }
}