package com.kosta.saladMan.repository.chat;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.chat.ChatRoom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
 List<ChatRoom> findByIsGroupChat(String isGroupChat);

 @Query(
   value = "SELECT cr.* FROM chat_room cr " +
           "WHERE cr.is_group_chat = 'N' AND cr.id IN (" +
           "    SELECT cp.chat_room_id FROM chat_participant cp " +
           "    WHERE cp.store_id = :store1Id OR cp.store_id = :store2Id " +
           "    GROUP BY cp.chat_room_id " +
           "    HAVING COUNT(DISTINCT cp.store_id) = 2" +
           ") LIMIT 1",
   nativeQuery = true
 )
 Optional<ChatRoom> findPrivateRoomBetweenStores(
     @Param("store1Id") Integer store1Id,
     @Param("store2Id") Integer store2Id
 );

 Optional<ChatRoom> findByIsGroupChatAndName(String isGroupChat, String name);
}
