
package com.quiz.onlinequiz.dto;

public record QuizQuestion(
        Long id,
        String questionTitle,
        String option1,
        String option2,
        String option3,
        String option4
) {
}

