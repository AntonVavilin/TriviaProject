package demo.game;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {
    Page<GameResult> findByDifficultyOrderByCorrectAnswersDescDurationMsAscFinishedAtDesc(
            Difficulty difficulty, Pageable pageable);}