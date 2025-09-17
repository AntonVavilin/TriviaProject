package demo.player;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/trivia/players")
public class PlayerController {
    private final PlayerService playerService;
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public String listPlayers(Model model) {
        model.addAttribute("players", playerService.findAll());
        return "players";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Player player = playerService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid player Id:" + id));
        model.addAttribute("player", player);
        return "player-form";
    }


    @PostMapping("/{id}")
    public String updatePlayer(@PathVariable Long id, @ModelAttribute Player player) {
        playerService.update(id, player);
        return "redirect:/trivia/players";
    }


    @GetMapping("/delete/{id}")
    public String deletePlayer(@PathVariable Long id) {
        playerService.deleteById(id);
        return "redirect:/trivia/players";
    }

}
