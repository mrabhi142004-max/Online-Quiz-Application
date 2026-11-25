
package com.quiz.onlinequiz.controller;

import java.util.List;

import com.quiz.onlinequiz.entity.Question;
import com.quiz.onlinequiz.repository.QuestionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;

    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    // 1. Add a new question
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Question addQuestion(@RequestBody Question question) {

        question.setId(null);

        return questionRepository.save(question);
    }

    // 2. Get all questions
    @GetMapping
    public List<Question> getAllQuestions() {

        return questionRepository.findAll();
    }

    // 3. Get question by ID
    @GetMapping("/{id}")
    public Question getQuestionById(@PathVariable Long id) {

        return questionRepository.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Question not found"
                    )
                );
    }
    
@PutMapping("/{id}")
public Question updateQuestion(
        @PathVariable Long id,
        @RequestBody Question updatedQuestion) {

    Question question = questionRepository.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Question not found"
                )
            );

    question.setQuestionTitle(updatedQuestion.getQuestionTitle());
    question.setOption1(updatedQuestion.getOption1());
    question.setOption2(updatedQuestion.getOption2());
    question.setOption3(updatedQuestion.getOption3());
    question.setOption4(updatedQuestion.getOption4());
    question.setCorrectAnswer(updatedQuestion.getCorrectAnswer());

    return questionRepository.save(question);
}

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteQuestion(@PathVariable Long id) {

    if (!questionRepository.existsById(id)) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Question not found"
        );
    }

    questionRepository.deleteById(id);
}

@PostMapping("/bulk")
@ResponseStatus(HttpStatus.CREATED)
public List<Question> addMultipleQuestions(
        @RequestBody List<Question> questions) {

    if (questions == null || questions.isEmpty()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Questions list cannot be empty"
        );
    }

    for (Question question : questions) {
        if (question == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Question cannot be null"
            );
        }

        question.setId(null);
    }

   return questionRepository.saveAll(questions);
}


}


