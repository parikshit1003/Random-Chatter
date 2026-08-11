package org.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("sessions")
@AllArgsConstructor
@NoArgsConstructor
public class ActiveSession {
    @Id
    private String sessionID;
    private String roomID;
}
