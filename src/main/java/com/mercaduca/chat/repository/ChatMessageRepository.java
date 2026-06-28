package com.mercaduca.chat.repository;

import com.mercaduca.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByConversationIdOrderByCreatedAtAsc(String conversationId, Pageable pageable);

    /**
     * Retorna el ultimo mensaje de cada conversacion del usuario.
     * Usamos un subquery MAX(id) para obtener el mensaje mas reciente por
     * conversacion; desde ese mensaje extraemos sender/recipient para
     * mostrar el nombre del otro participante en la bandeja.
     */
    @Query("SELECT c FROM ChatMessage c " +
           "WHERE c.id IN (" +
           "  SELECT MAX(c2.id) FROM ChatMessage c2 " +
           "  WHERE c2.sender.id = :userId OR c2.recipient.id = :userId " +
           "  GROUP BY c2.conversationId" +
           ") ORDER BY c.createdAt DESC")
    List<ChatMessage> findLastMessagesPerConversation(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM ChatMessage c " +
           "WHERE c.conversationId = :convId " +
           "AND c.recipient.id = :userId AND c.read = false")
    long countUnread(@Param("convId") String conversationId,
                     @Param("userId") Long userId);

    long countBySenderIdAndRecipientIdAndReadFalse(Long senderId, Long recipientId);

    @Modifying
    @Query("UPDATE ChatMessage c SET c.read = true " +
           "WHERE c.conversationId = :convId AND c.recipient.id = :userId")
    void markConversationAsRead(@Param("convId") String conversationId,
                                @Param("userId") Long userId);
}
