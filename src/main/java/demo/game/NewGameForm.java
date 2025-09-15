
package demo.game;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewGameForm {
    @NotBlank
    @Size(max = 64)
    private String playerName;

    @NotNull
    private Difficulty difficulty;

//    public String getPlayerName() {
//        return playerName;
//    }
//
//    public void setPlayerName(String playerName) {
//        this.playerName = playerName;
//    }
//
//    public Difficulty getDifficulty() {
//        return difficulty;
//    }
//
//    public void setDifficulty(Difficulty difficulty) {
//        this.difficulty = difficulty;
//    }
}
