package com.mercaduca.products.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;
public class QuestionDTOs {
    @Data public static class AskQuestionRequest {
        @NotBlank(message = "La pregunta no puede estar vacía") private String question;
    }
    @Data public static class AnswerQuestionRequest {
        @NotBlank(message = "La respuesta no puede estar vacía") private String answer;
    }
    @Data public static class QuestionResponse {
        private Long id; private Long productId; private String productTitle;
        private Long buyerId; private String buyerName; private String question;
        private String answer; private boolean answered; private LocalDateTime createdAt;
    }
}
