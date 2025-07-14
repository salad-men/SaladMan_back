package com.kosta.saladMan.repository.chat;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.chat.ChatMessage;
import com.kosta.saladMan.entity.chat.ChatRoom;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Integer> {

	@Query("select m from ChatMessage m join fetch m.store join fetch m.chatRoom where m.chatRoom = :chatRoom order by m.createdTime asc")
	List<ChatMessage> findByChatRoomOrderByCreatedTimeAscWithStore(@Param("chatRoom") ChatRoom chatRoom);}
