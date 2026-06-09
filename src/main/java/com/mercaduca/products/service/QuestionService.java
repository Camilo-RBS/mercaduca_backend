package com.mercaduca.products.service;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.products.dto.QuestionDTOs;
import org.springframework.data.domain.Pageable;
public interface QuestionService {
    QuestionDTOs.QuestionResponse askQuestion(Long productId, QuestionDTOs.AskQuestionRequest request, Long buyerId);
    QuestionDTOs.QuestionResponse answerQuestion(Long questionId, QuestionDTOs.AnswerQuestionRequest request, Long sellerId);
    PageResponse<QuestionDTOs.QuestionResponse> getProductQuestions(Long productId, Pageable pageable);
    PageResponse<QuestionDTOs.QuestionResponse> getUnansweredQuestions(Long productId, Pageable pageable);
}
