
package com.quiz.onlinequiz.repository;

import com.quiz.onlinequiz.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository
        extends JpaRepository<Question, Long> {

    List<Question> findByCategoryIgnoreCase(
        String category
    );

    List<Question> findByCategoryIgnoreCaseAndDifficultyIgnoreCase(
        String category,
        String difficulty
    );
}




