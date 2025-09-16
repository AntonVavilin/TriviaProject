# CREATE TABLE player (
#                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
#                         username VARCHAR(64) NOT NULL UNIQUE,
#                         email VARCHAR(128),
#                         registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
# );
#
# -- Game result table
# CREATE TABLE game_result (
#                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
#                              player_id BIGINT NOT NULL,
#                              difficulty VARCHAR(16) NOT NULL,
#                              total_questions INT NOT NULL,
#                              correct_answers INT NOT NULL,
#                              duration_ms BIGINT NOT NULL,
#                              finished_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
#                              CONSTRAINT fk_player FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
# );
#
# -- Question table
# CREATE TABLE question (
#                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
#                           category VARCHAR(128),
#                           difficulty VARCHAR(16),
#                           question_text TEXT NOT NULL,
#                           correct_answer VARCHAR(255) NOT NULL
# );
#
# -- Game-question join table
# CREATE TABLE game_question (
#                                game_id BIGINT NOT NULL,
#                                question_id BIGINT NOT NULL,
#                                chosen_answer VARCHAR(255),
#                                is_correct BOOLEAN,
#                                PRIMARY KEY (game_id, question_id),
#                                FOREIGN KEY (game_id) REFERENCES game_result(id) ON DELETE CASCADE,
#                                FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE
# );
#
# # CREATE INDEX idx_game_result_score ON game_result (difficulty, correct_answers DESC, duration_ms, finished_at DESC);