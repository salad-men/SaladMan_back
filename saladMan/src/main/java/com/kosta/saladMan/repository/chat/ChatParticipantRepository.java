package com.kosta.saladMan.repository.chat;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kosta.saladMan.entity.chat.ChatParticipant;
import com.kosta.saladMan.entity.chat.ChatRoom;
import com.kosta.saladMan.entity.store.Store;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Integer> {

    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);
    Optional<ChatParticipant> findByChatRoomAndStore(ChatRoom chatRoom, Store store);

    List<ChatParticipant> findAllByStore(Store store);

    @Query("SELECT cp1.chatRoom FROM ChatParticipant cp1 JOIN ChatParticipant cp2 " +
    		"ON cp1.chatRoom.id = cp2.chatRoom.id " +
            "WHERE cp1.store.id = :myId " +
            "AND cp2.store.id = :otherStoreId " +
            "AND cp1.chatRoom.isGroupChat='N'")
    Optional<ChatRoom> findExistingPrivateRoom(@Param("myId") Integer integer, @Param("otherStoreId") Integer integer2);

}
