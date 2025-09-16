package demo.question;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GameQuestionRepository extends JpaRepository<GameQuestion, Long> {
}
