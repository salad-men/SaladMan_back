package com.kosta.saladMan.repository.chat;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.chat.ChatMessage;
import com.kosta.saladMan.entity.chat.ChatRoom;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Integer> {

    List<ChatMessage> findByChatRoomOrderByCreatedTimeAsc(ChatRoom chatRoom);
}
