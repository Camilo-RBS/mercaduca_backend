package com.mercaduca.products.service;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.exceptions.custom.ForbiddenException;
import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.products.dto.QuestionDTOs;
import com.mercaduca.products.entity.Product;
import com.mercaduca.products.entity.ProductQuestion;
import com.mercaduca.products.repository.ProductQuestionRepository;
import com.mercaduca.products.repository.ProductRepository;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final ProductQuestionRepository questionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    @Override @Transactional
    public QuestionDTOs.QuestionResponse askQuestion(Long productId, QuestionDTOs.AskQuestionRequest req, Long buyerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto","id",productId));
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario","id",buyerId));
        ProductQuestion q = ProductQuestion.builder().product(product).buyer(buyer)
                .question(req.getQuestion()).answered(false).build();
        return toResponse(questionRepository.save(q));
    }
    @Override @Transactional
    public QuestionDTOs.QuestionResponse answerQuestion(Long questionId, QuestionDTOs.AnswerQuestionRequest req, Long sellerId) {
        ProductQuestion q = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta","id",questionId));
        if (!q.getProduct().getSeller().getId().equals(sellerId))
            throw new ForbiddenException("Solo puedes responder preguntas de tus propios productos");
        q.setAnswer(req.getAnswer()); q.setAnswered(true);
        return toResponse(questionRepository.save(q));
    }
    @Override @Transactional(readOnly = true)
    public PageResponse<QuestionDTOs.QuestionResponse> getProductQuestions(Long productId, Pageable pageable) {
        return PageResponse.from(questionRepository.findByProductId(productId, pageable).map(this::toResponse)); }
    @Override @Transactional(readOnly = true)
    public PageResponse<QuestionDTOs.QuestionResponse> getUnansweredQuestions(Long productId, Pageable pageable) {
        return PageResponse.from(questionRepository.findByProductIdAndAnsweredFalse(productId, pageable).map(this::toResponse)); }
    private QuestionDTOs.QuestionResponse toResponse(ProductQuestion q) {
        QuestionDTOs.QuestionResponse r = new QuestionDTOs.QuestionResponse();
        r.setId(q.getId()); r.setProductId(q.getProduct().getId()); r.setProductTitle(q.getProduct().getTitle());
        r.setBuyerId(q.getBuyer().getId()); r.setBuyerName(q.getBuyer().getFirstName()+" "+q.getBuyer().getLastName());
        r.setQuestion(q.getQuestion()); r.setAnswer(q.getAnswer()); r.setAnswered(q.isAnswered()); r.setCreatedAt(q.getCreatedAt());
        return r;
    }
}
