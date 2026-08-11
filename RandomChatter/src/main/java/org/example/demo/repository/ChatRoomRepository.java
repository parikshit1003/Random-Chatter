package org.example.demo.repository;

import org.example.demo.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    @Query("{ 'occupancy': 1}")
    List<ChatRoom> findAvailableChatRoom();
    @Update("{ '$set' : { 'occupancy' : 2 } }")
    @Query("{ '_id' : ?0 }")
    void updateChatRoomOccupancy(String id);
    @Update("{ '$set' : { 'tokenA' : ?1 } }")
    @Query("{ '_id' : ?0 }")
    void updateChatRoomTokenA(String id, String token);
    @Update("{ '$set' : { 'tokenB' : ?1 } }")
    @Query("{ '_id' : ?0 }")
    void updateChatRoomTokenB(String id, String token);
    @Update("{ '$set' : { 'messageListA' : ?1 } }")
    @Query("{ '_id' : ?0 }")
    void updateChatListA(String id, List<String> messageList);
    @Update("{ '$set' : { 'messageListB' : ?1 } }")
    @Query("{ '_id' : ?0 }")
    void updateChatListB(String id, List<String> messageList);

}
