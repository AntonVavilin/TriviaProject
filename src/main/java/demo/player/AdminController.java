package demo.player;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {
    @GetMapping("/trivia/admin-login")
    public String adminLogin() {
        return "admin-login";
    }

    @PostMapping("/trivia/admin-login")
    public String adminLogin(@RequestParam String password, HttpSession session) {
        if ("Trivia123".equals(password)) {
            session.setAttribute("ADMIN_LOGGED_IN", true);
            return "redirect:/trivia";
        }
        return "admin-login";
    }
}
