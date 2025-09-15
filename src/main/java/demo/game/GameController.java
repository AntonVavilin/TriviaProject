package demo.game;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/trivia")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final GameResultRepository repo;

    @GetMapping
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/new")
    public String newGame(Model model) {
        model.addAttribute("form", new NewGameForm());
        return "new-game";
    }

    @PostMapping("/new")
    public String startGame(@Valid @ModelAttribute("form")
                            NewGameForm form,
                            BindingResult br,
                            HttpSession httpSession) {
        if (br.hasErrors()) return "new-game";
        var session = gameService.startNewGame(form.getPlayerName(), form.getDifficulty());
        httpSession.setAttribute("GAME_SESSION", session);
        return "redirect:/trivia/play";
    }

    @GetMapping("/play")
    public String play(HttpSession httpSession, Model model, RedirectAttributes ra) {
        var s = (GameSession) httpSession.getAttribute("GAME_SESSION");
        if (s == null) return "redirect:/trivia";
        if (s.isFinished()) return "redirect:/trivia/finish";
        var q = s.getQuestions().get(s.getIndex());
        model.addAttribute("question", q);
        model.addAttribute("index", s.getIndex() + 1);
        model.addAttribute("total", s.getQuestions().size());
        model.addAttribute("answerForm", new AnswerForm());
        return "play";
    }

    @PostMapping("/answer")
    public String answer(@Valid @ModelAttribute AnswerForm answerForm,
                         BindingResult br,
                         HttpSession httpSession) {
        if (br.hasErrors()) return "redirect:/trivia/play";
        var s = (GameSession) httpSession.getAttribute("GAME_SESSION");
        if (s == null) return "redirect:/trivia";
        gameService.answer(s, answerForm.getOptionId());
        httpSession.setAttribute("GAME_SESSION", s); // update
        return s.isFinished() ? "redirect:/trivia/finish" : "redirect:/trivia/play";
    }

    @GetMapping("/finish")
    public String finish(HttpSession httpSession, Model model) {
        var s = (GameSession) httpSession.getAttribute("GAME_SESSION");
        if (s == null) return "redirect:/trivia";
        model.addAttribute("playerName", s.getPlayerName());
        model.addAttribute("score", s.getCorrectCount());
        model.addAttribute("total", s.getQuestions().size());
        model.addAttribute("difficulty", s.getDifficulty());
        httpSession.removeAttribute("GAME_SESSION");
        return "finish";
    }

    @GetMapping("/scoreboard")
    public String scoreboard(@RequestParam(defaultValue = "EASY") Difficulty difficulty,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        Page<GameResult> results = repo
                .findByDifficultyOrderByCorrectAnswersDescDurationMsAscFinishedAtDesc(difficulty, PageRequest.of(page, 20));
        model.addAttribute("results", results);
        model.addAttribute("difficulty", difficulty);
        return "scoreboard";
    }
}


