package com.kosta.saladMan.repository.chatbot;

import com.kosta.saladMan.entity.chatbot.ChatbotQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatbotQuestionRepository extends JpaRepository<ChatbotQuestion, Integer> {
    List<ChatbotQuestion> findByMainOptionIdOrderByDisplayOrderAsc(int mainOptionId);
}
