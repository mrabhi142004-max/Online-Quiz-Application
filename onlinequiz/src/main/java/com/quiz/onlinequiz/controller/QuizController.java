
package com.quiz.onlinequiz.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import com.quiz.onlinequiz.dto.QuizAnswer;
import com.quiz.onlinequiz.dto.QuizQuestion;
import com.quiz.onlinequiz.dto.QuizResult;
import com.quiz.onlinequiz.entity.Question;
import com.quiz.onlinequiz.repository.QuestionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuestionRepository questionRepository;

    public QuizController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

@GetMapping("/questions")
public List<QuizQuestion> getQuizQuestions(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String difficulty) {

    List<Question> questions;

    if (category != null && !category.isBlank()
            && difficulty != null && !difficulty.isBlank()) {

        
        questions = questionRepository
            .findByCategoryIgnoreCaseAndDifficultyIgnoreCase(
                category, difficulty
            );

    } else if (category != null && !category.isBlank()) {

        
        questions = questionRepository
            .findByCategoryIgnoreCase(category);

    } else if (difficulty != null && !difficulty.isBlank()) {

      
        questions = questionRepository.findAll()
            .stream()
            .filter(q -> q.getDifficulty() != null
                && q.getDifficulty().equalsIgnoreCase(difficulty))
            .toList();

    } else {

        
        questions = questionRepository.findAll();
    }

    return questions.stream()
        .map(q -> new QuizQuestion(
            q.getId(),
            q.getQuestionTitle(),
            q.getOption1(),
            q.getOption2(),
            q.getOption3(),
            q.getOption4()
        ))
        .toList();
}


@PostMapping("/submit")
public QuizResult submitQuiz(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String difficulty,
        @RequestBody List<QuizAnswer> answers) {

    List<Question> questions;

  
    if (category != null && !category.isBlank()
            && difficulty != null && !difficulty.isBlank()) {

        questions = questionRepository
            .findByCategoryIgnoreCaseAndDifficultyIgnoreCase(
                category, difficulty
            );

    } else if (category != null && !category.isBlank()) {

        questions = questionRepository
            .findByCategoryIgnoreCase(category);

    } else if (difficulty != null && !difficulty.isBlank()) {

        questions = questionRepository.findAll()
            .stream()
            .filter(q -> q.getDifficulty() != null
                && q.getDifficulty().equalsIgnoreCase(difficulty))
            .toList();

    } else {
        questions = questionRepository.findAll();
    }

   
    if (questions.isEmpty()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "No questions available for selected filters"
        );
    }


    Map<Long, Question> questionMap = new HashMap<>();

    for (Question question : questions) {
        questionMap.put(question.getId(), question);
    }


    if (answers == null) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Answers cannot be null"
        );
    }

    int correct = 0;
    Set<Long> submittedIds = new HashSet<>();

    for (QuizAnswer answer : answers) {

        if (answer == null
                || answer.questionId() == null
                || answer.selectedAnswer() == null
                || answer.selectedAnswer().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid answer"
            );
        }

        Long questionId = answer.questionId();

        if (!questionMap.containsKey(questionId)
                || !submittedIds.add(questionId)) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid or duplicate question ID"
            );
        }

        Question question = questionMap.get(questionId);

        if (question.getCorrectAnswer() != null
                && question.getCorrectAnswer()
                    .equalsIgnoreCase(
                        answer.selectedAnswer().trim()
                    )) {
            correct++;
        }
    }


    if (submittedIds.size() != questions.size()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Please answer all questions"
        );
    }

 
    int total = questions.size();
    int wrong = total - correct;
    double percentage = (correct * 100.0) / total;

    return new QuizResult(
        total,
        correct,
        wrong,
        percentage
    );
}


}

