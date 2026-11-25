
package com.quiz.onlinequiz.dto;

public record QuizResult(
        int totalQuestions,
        int correctAnswers,
        int wrongAnswers,
        double percentage
) {
}

