package com.kosta.saladMan.repository.chatbot;

import com.kosta.saladMan.entity.chatbot.ChatbotMainOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatbotMainOptionRepository extends JpaRepository<ChatbotMainOption, Long> {
    List<ChatbotMainOption> findAllByOrderByOrderAsc(); // 정렬순으로 전체 불러오기
}
