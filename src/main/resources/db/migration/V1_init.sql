CREATE TABLE game_result (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             player_name VARCHAR(64) NOT NULL,
                             difficulty VARCHAR(16) NOT NULL,
                             total_questions INT NOT NULL,
                             correct_answers INT NOT NULL,
                             duration_ms BIGINT NOT NULL,
                             finished_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_game_result_score ON game_result (difficulty, correct_answers DESC, duration_ms, finished_at DESC);