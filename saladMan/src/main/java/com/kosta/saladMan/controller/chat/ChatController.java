package com.kosta.saladMan.controller.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kosta.saladMan.dto.chat.ChatMessageDto;
import com.kosta.saladMan.dto.chat.ChatRoomListResDto;
import com.kosta.saladMan.dto.chat.MyChatListResDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.service.chat.ChatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
	private final StoreRepository storeRepository;

    public ChatController(ChatService chatService, StoreRepository storeRepository ) {
        this.chatService = chatService;
		this.storeRepository = storeRepository;
    }

    //그룹 채팅방 개설
    @PostMapping("/room/group/create")
    public ResponseEntity<?> createGroupRoom(@RequestParam String roomName) {
        chatService.createGroupRoom(roomName);
        return ResponseEntity.ok().build();
    }


    //그룹채팅목록 조회
    @GetMapping("/room/group/list")
    public ResponseEntity<?> getGroupChatRooms() {
        List<ChatRoomListResDto> chatRooms = chatService.getGroupchatRoom();
        return  new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    //그룹 채팅방 참여
    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<?> joinGroupChatRoom(@PathVariable Integer roomId) {
        chatService.addParticipantToGroupChat(roomId);
        return ResponseEntity.ok().build();
    }

    //이전 메시지 조회
    @GetMapping("/history/{roomId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Integer roomId) {
        List<ChatMessageDto> chatMessageDtos = chatService.getChatHistory(roomId);
        return new ResponseEntity<>(chatMessageDtos, HttpStatus.OK);
    }

    //채팅메시지 읽음처리
    @PostMapping("/room/{roomId}/read")
    public ResponseEntity<?> messageRead(@PathVariable Integer roomId) {
            chatService.messageRead(roomId);
            return ResponseEntity.ok().build();
    }
    
    //내 채팅방 목록조회 : roomId, roomName, 그룹채팅 여부, 메시지 읽음개수
    @GetMapping("my/rooms")
    public ResponseEntity<?> getMyChatRooms() {

        List<MyChatListResDto> myChatListResDtos = chatService.getMyChatRooms();

        return new ResponseEntity<>(myChatListResDtos, HttpStatus.OK);
    }

    //채팅방 나가기
    @DeleteMapping("/room/group/{roomId}/leave")
    public ResponseEntity<?> leaveGroupChatRoom(@PathVariable Integer roomId) {
        chatService.leaveGroupChatRoom(roomId);
        return ResponseEntity.ok().build();
    }

    //개인 채팅방 개설 또는 기존 roomId return
    @PostMapping("/room/private/create")
    public ResponseEntity<?> GetOrCreatePrivateRoom(@RequestParam Integer otherStoreId) {
        try {
            System.out.println("[ChatController] POST /chat/room/private/create, otherStoreId=" + otherStoreId);
            Integer roomId = chatService.getOrCreatePrivateRoom(otherStoreId);
            System.out.println("[ChatController] 채팅방 생성/조회 성공, roomId=" + roomId);
            return new ResponseEntity<>(roomId, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("[ChatController][에러] 채팅방 생성 실패: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("채팅방 생성 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    //매장 조회
    @GetMapping("/stores")
    public ResponseEntity<Map<String, Object>> getAllStoresForChat() {
        List<Store> stores = storeRepository.findAll();
        // 클라이언트로 노출할 정보만 추리기 
        List<Map<String, Object>> storeList = new ArrayList<>();
        for (Store s : stores) {
            storeList.add(Map.of(
                "id", s.getId(),
                "name", s.getName(),
                "username", s.getUsername(),
                "role", s.getRole()
            ));
        }
        return ResponseEntity.ok(Map.of(
            "stores", storeList
        ));
    }


}