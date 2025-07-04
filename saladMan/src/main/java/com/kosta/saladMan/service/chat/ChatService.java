package com.kosta.saladMan.service.chat;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.saladMan.dto.chat.ChatMessageDto;
import com.kosta.saladMan.dto.chat.ChatParticipantDto;
import com.kosta.saladMan.dto.chat.ChatRoomListResDto;
import com.kosta.saladMan.dto.chat.MyChatListResDto;
import com.kosta.saladMan.entity.chat.ChatMessage;
import com.kosta.saladMan.entity.chat.ChatParticipant;
import com.kosta.saladMan.entity.chat.ChatRoom;
import com.kosta.saladMan.entity.chat.ReadStatus;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.chat.ChatMessageRepository;
import com.kosta.saladMan.repository.chat.ChatParticipantRepository;
import com.kosta.saladMan.repository.chat.ChatRoomRepository;
import com.kosta.saladMan.repository.chat.ReadStatusRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final StoreRepository storeRepository;
    private final ChatSseService chatSseService; 


    public ChatService(ChatRoomRepository chatRoomRepository, ChatParticipantRepository chatParticipantRepository, ChatMessageRepository chatMessageRepository, ReadStatusRepository readStatusRepository, StoreRepository storeRepository, ChatSseService chatSseService) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.readStatusRepository = readStatusRepository;
        this.storeRepository = storeRepository;
		this.chatSseService = chatSseService;
    }

    // 채팅 메시지 저장
    public void saveMessage(Integer roomId, ChatMessageDto chatMessageDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room cannot found"));

        Store sender = storeRepository.findByUsername(chatMessageDto.getSenderUsername())
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        ChatMessage chatMessage = chatMessageDto.toEntity(chatRoom, sender);
        chatMessageRepository.save(chatMessage);
        
        // 사용자별로 읽음 여부 저장
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for (ChatParticipant c : chatParticipants) {
            ReadStatus readStatus = ReadStatus.builder()
                    .chatRoom(chatRoom)
                    .store(c.getStore())
                    .chatMessage(chatMessage)
                    .isRead(c.getStore().equals(sender))
                    .build();
            readStatusRepository.save(readStatus);
        }
        
        ChatMessageDto sseDto = chatMessage.toDto(); 

        //SSE전송
        Set<String> participants = chatParticipants.stream()
                .map(cp -> cp.getStore().getUsername())
                .collect(Collectors.toSet());
        chatSseService.sendToUsers(participants, "newMessage", sseDto);

    }

    // 그룹채팅방 개설
    public void createGroupRoom(String chatRoomName) {
    	//매장 조회
        Store store = storeRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Store cannot found"));
        //채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomName)
                .isGroupChat("Y")
                .store(store)
                .build();
        chatRoomRepository.save(chatRoom);

        //채팅 참여자로 개설자를 추가(최초)
        ChatParticipantDto participantDto = ChatParticipantDto.builder()
                .chatRoomId(chatRoom.getId())
                .storeId(store.getId())
                .build();
        ChatParticipant chatParticipant = participantDto.toEntity(chatRoom, store);
        chatParticipantRepository.save(chatParticipant);
    }

    // 단체 채팅방 조회 
    public List<ChatRoomListResDto> getGroupchatRoom() {
        List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");
        List<ChatRoomListResDto> dtos = new ArrayList<>();
        for (ChatRoom chatRoom : chatRooms) {
            dtos.add(chatRoom.toDto());
        }
        return dtos;
    }

    // 참여자 추가
    public void addParticipantToGroupChat(Integer roomId) {
    	//채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room cannot be found"));
        //매장 조회
        Store store = storeRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Store cannot found"));

        //그룹챗이 아니면 참여자 추가X
        if (chatRoom.getIsGroupChat().equals("N")) {
            throw new IllegalArgumentException("그룹채팅이 아닙니다");
        }

        //이미 참여자 인지 검증
        Optional<ChatParticipant> participant =
                chatParticipantRepository.findByChatRoomAndStore(chatRoom, store);
        if (!participant.isPresent()) {
            addParticipantToRoom(chatRoom, store);
        }
    }

    // ChatParticipant 생성/저장 
    public void addParticipantToRoom(ChatRoom chatRoom, Store store) {
    	ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .store(store)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    // 이전 내역(메시지) 조회
    public List<ChatMessageDto> getChatHistory(Integer roomId) {
    	
        //내가 해당 채팅방의 참여자가 아닐 경우 에러(채팅방, 멤버 조회, 채팅방의 참여자조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room cannot be found"));
        Store store = storeRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Store cannot found"));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        
        boolean check = false;
        for(ChatParticipant chatParticipant : chatParticipants){
            if(chatParticipant.getStore().equals(store)){
                check = true;
            }
        }

        if(!check) throw new IllegalArgumentException("본인이 속하지 않은 채팅방 입니다");

        //특정 room에 대한 message 조회(생성순)
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom);
        List<ChatMessageDto> chatMessageDtos = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessages) {
            chatMessageDtos.add(chatMessage.toDto());
        }
        return chatMessageDtos;
    }

    // 채팅방 참여자인지 확인
    public boolean isRoomParticipant(String username, Integer roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room cannot be found"));
        Store store = storeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Store cannot found"));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for(ChatParticipant chatParticipant : chatParticipants){
            if(chatParticipant.getStore().equals(store)){
                return true;
            }
        }
        //해당 방에 해당 참여자가 아닌 경우
        return false;
    }

    // 메시지 읽음 처리
    public void messageRead(Integer roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room cannot be found"));
        Store store = storeRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Store cannot found"));

        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndStore(chatRoom, store);
        for (ReadStatus readStatus : readStatuses) {
            readStatus.updateIsRead(true);
        }
    }

    // 내 채팅방 목록 조회 
    public List<MyChatListResDto> getMyChatRooms() {
        Store store = storeRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Store cannot found"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByStore(store);
        
        List<MyChatListResDto> chatListResDtos = new ArrayList<>();
        
        for (ChatParticipant chatParticipant : chatParticipants) {
            Integer count = readStatusRepository.countByChatRoomAndStoreAndIsReadFalse(chatParticipant.getChatRoom(), store);
            MyChatListResDto dto = MyChatListResDto.builder()
                    .roomId(chatParticipant.getChatRoom().getId())
                    .roomName(chatParticipant.getChatRoom().getName())
                    .isGroupChat(chatParticipant.getChatRoom().getIsGroupChat())
                    .unReadCount(count)
                    .storeId(chatParticipant.getChatRoom().getStore().getId())
                    .build();
            chatListResDtos.add(dto);
        }
        return chatListResDtos;
    }

    // 채팅방 나가기
    public void leaveGroupChatRoom(Integer roomId) {
        //참여자 객체 지우기
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room cannot be found"));
        Store store = storeRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Store cannot found"));
        //단체 채팅방인지 확인
        if (chatRoom.getIsGroupChat().equals("N")) {
            throw new IllegalArgumentException("단체 채팅방이 아닙니다");
        }
        ChatParticipant c = chatParticipantRepository.findByChatRoomAndStore(chatRoom, store)
                .orElseThrow(() -> new EntityNotFoundException("참여자 찾을 수 없습니다."));
        chatParticipantRepository.delete(c);
        
        //남은 참여자 수 체크
        //모두가 나가면 채팅방, 메세지, 읽음여부 모두 삭제
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        if (chatParticipants.isEmpty()) {
            chatRoomRepository.delete(chatRoom); // cascade로 모두 삭제
        }
    }

    // 개인 채팅방 가져오거나 만들거나 
    public Integer getOrCreatePrivateRoom(Integer otherStoreId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Store me = storeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));
        Store other = storeRepository.findById(otherStoreId)
                .orElseThrow(() -> new EntityNotFoundException("Other not found"));

        // 1) 기존에 둘 다 참가중인 방이 있는지 (participant 기반) 확인
        Optional<ChatRoom> roomOpt = chatRoomRepository
            .findPrivateRoomBetweenStores(me.getId(), other.getId());
        if (roomOpt.isPresent()) {
            ChatRoom room = roomOpt.get();
            if (!chatParticipantRepository.existsByChatRoomAndStore(room, me)) {
                addParticipantToRoom(room, me);
            }
            return room.getId();
        }

        // 2) fallback: 방 이름(A-B 또는 B-A) 기반 조회
        String name1 = me.getName() + "-" + other.getName();
        String name2 = other.getName() + "-" + me.getName();
        Optional<ChatRoom> fallback = chatRoomRepository
            .findByIsGroupChatAndName("N", name1);
        if (!fallback.isPresent()) {
            fallback = chatRoomRepository.findByIsGroupChatAndName("N", name2);
        }
        if (fallback.isPresent()) {
            ChatRoom room = fallback.get();
            if (!chatParticipantRepository.existsByChatRoomAndStore(room, me)) {
                addParticipantToRoom(room, me);
            }
            return room.getId();
        }

        // 3) 없으면 새로 생성
        ChatRoom newRoom = ChatRoom.builder()
            .isGroupChat("N")
            .name(name1)
            .store(me)
            .build();
        chatRoomRepository.save(newRoom);

        addParticipantToRoom(newRoom, me);
        addParticipantToRoom(newRoom, other);
        return newRoom.getId();
    }


    public void leavePrivateChatRoom(Integer roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room cannot be found"));
        Store store = storeRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Store cannot found"));

        // 참여자만 삭제 (메시지/방/상대방은 유지)
        ChatParticipant participant = chatParticipantRepository.findByChatRoomAndStore(chatRoom, store)
            .orElseThrow(() -> new EntityNotFoundException("참여자 찾을 수 없습니다."));
        chatParticipantRepository.delete(participant);

        // **모든 참여자가 나갔으면 방/메시지도 삭제**
        if (chatParticipantRepository.findByChatRoom(chatRoom).isEmpty()) {
            chatRoomRepository.delete(chatRoom); // Cascade로 메시지, 읽음상태 등도 삭제
        }
    }
    
    
    public Set<String> getParticipantUsernames(Integer roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room cannot be found"));
        List<ChatParticipant> participants = chatParticipantRepository.findByChatRoom(chatRoom);
        return participants.stream()
            .map(cp -> cp.getStore().getUsername())
            .collect(Collectors.toSet());
    }





}
