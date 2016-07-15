package com.zepinos.chat.server.Domain.Repository;

import com.zepinos.chat.server.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
