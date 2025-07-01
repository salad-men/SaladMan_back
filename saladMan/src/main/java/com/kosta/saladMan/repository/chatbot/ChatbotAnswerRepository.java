package com.kosta.saladMan.repository.chatbot;

import com.kosta.saladMan.entity.chatbot.ChatbotAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChatbotAnswerRepository extends JpaRepository<ChatbotAnswer, Integer> {
	Optional<ChatbotAnswer> findByValueKey(String valueKey);

}
