package org.example.demo.controller;

import org.example.demo.model.ActiveSession;
import org.example.demo.model.ChatRoom;
import org.example.demo.model.MessageResponse;
import org.example.demo.repository.ActiveSessionRepository;
import org.example.demo.repository.ChatRoomRepository;
import org.example.demo.util.GenerateSessionToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class MessageController {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    ActiveSessionRepository activeSessionRepository;

    int ct = 0;

    int ct1 = 0;

    @GetMapping("/init")
    public String init(){
        String token = GenerateSessionToken.generate();

        // Find first chat room with occupancy == 1
        List<ChatRoom> availableRoomList = chatRoomRepository.findAvailableChatRoom();
        ChatRoom availableRoom = null;

        if (availableRoomList != null && !availableRoomList.isEmpty()) {
            availableRoom = availableRoomList.get(0);
        }

        if (availableRoom == null) {
            availableRoom = chatRoomRepository.save(new ChatRoom(null, 1, token, null, new ArrayList<>(), new ArrayList<>()));
        } else {
            chatRoomRepository.updateChatRoomTokenB(availableRoom.getId(), token);
            chatRoomRepository.updateChatRoomOccupancy(availableRoom.getId());
        }
        activeSessionRepository.save(new ActiveSession(token, availableRoom.getId()));

        System.out.println("Initialization request with token:- " + token + " ------ count: " + ct);

        ct++;

        return token;
    }

    @PostMapping("/messages/send/{message}")
    public void sendMessage(@RequestHeader("SessionToken") String sessionToken, @PathVariable String message){
        // Find room associated with this session token (ActiveSession collection)
        ActiveSession activeSession = activeSessionRepository.findById(sessionToken).orElse(null);

        if (activeSession == null) {
            return;
        }

        ChatRoom chatRoom = chatRoomRepository.findById(activeSession.getRoomID()).orElse(null);

        if (chatRoom == null) {
            return;
        }

        // Add new message in chat list associated with the mentioned token
        List<String> messages;
        if (sessionToken.equals(chatRoom.getTokenA())) {
            messages = chatRoom.getMessageListA();
            messages.add(message);
            chatRoomRepository.updateChatListA(chatRoom.getId(), messages);
        } else if (sessionToken.equals(chatRoom.getTokenB())) {
            messages = chatRoom.getMessageListB();
            messages.add(message);
            chatRoomRepository.updateChatListB(chatRoom.getId(), messages);
        }

        System.out.println("Send Message for token " + sessionToken);
    }

    @GetMapping("/messages/receive/{syncState}")
    public MessageResponse receiveMessages(@RequestHeader("SessionToken") String sessionToken, @PathVariable int syncState){
        // Find room associated with this session token (ActiveSession collection)
        ActiveSession activeSession = activeSessionRepository.findById(sessionToken).orElse(null);

        if (activeSession == null) {
            return null;
        }

        ChatRoom chatRoom = chatRoomRepository.findById(activeSession.getRoomID()).orElse(null);

        if (chatRoom == null) {
            return null;
        }

        // Check size of message list associated with the alternate token, if it exceeds syncState, send the delta
        List<String> messages;
        if (sessionToken.equals(chatRoom.getTokenA())) {
            messages = chatRoom.getMessageListB();
        } else if (sessionToken.equals(chatRoom.getTokenB())) {
            messages = chatRoom.getMessageListA();
        } else {
            return null;
        }

        if (messages.size() <= syncState) {
            return new MessageResponse(syncState, null);
        }

        List<String> result = new ArrayList<>();

        for (int i = syncState; i < messages.size(); i++) {
            result.add(messages.get(i));
        }

        System.out.println("Receive Message for token: " + sessionToken);

        // Send the updated sync state with new messages
        return new MessageResponse(messages.size(), result);
    }

    @DeleteMapping("/terminate")
    public void terminateSession(@RequestHeader("SessionToken") String sessionToken) {
        // Find Active Session associated with this session token
        ActiveSession activeSession = activeSessionRepository.findById(sessionToken).orElse(null);

        if (activeSession == null) {
            return;
        }

        // Find and remove room associated with this session token
        ChatRoom chatRoom = chatRoomRepository.findById(activeSession.getRoomID()).orElse(null);

        System.out.println("Deleting:- " + chatRoom.getId() + ", " + activeSession.getRoomID() + ", " + chatRoom.getTokenA() + ", " + chatRoom.getTokenB());

        if (chatRoom == null) {
            return;
        }

        if (chatRoom.getTokenA() != null) activeSessionRepository.deleteById(chatRoom.getTokenA());
        if (chatRoom.getTokenB() != null) activeSessionRepository.deleteById(chatRoom.getTokenB());
        chatRoomRepository.deleteById(chatRoom.getId());

        System.out.println("Termination request with token:- " + sessionToken + " ------ count: " + ct1);

        ct1++;
    }

}
