package com.messenger.repository;

import com.messenger.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {
    List<Server> findByCreatorPhoneNumber(String creatorPhoneNumber);
}
