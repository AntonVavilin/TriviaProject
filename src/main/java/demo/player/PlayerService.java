package demo.player;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id);
    }

    public Optional<Player> update(Long id, Player updatedPlayer) {
        return playerRepository.findById(id).map(existing -> {
            existing.setUsername(updatedPlayer.getUsername());
            return playerRepository.save(existing);
        });
    }

    public boolean deleteById(Long id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
