package org.example.demo.repository;

import org.example.demo.model.ActiveSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActiveSessionRepository extends MongoRepository<ActiveSession, String> {
}
