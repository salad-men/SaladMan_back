package com.kosta.saladMan.repository.chat;


import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.saladMan.entity.chat.ChatRoom;
import com.kosta.saladMan.entity.chat.ReadStatus;
import com.kosta.saladMan.entity.store.Store;

import java.util.List;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, Integer> {

    public List<ReadStatus> findByChatRoomAndStore(ChatRoom chatRoom, Store store);

    Integer countByChatRoomAndStoreAndIsReadFalse(ChatRoom chatRoom, Store store);

}
