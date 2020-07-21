package ru.selezneva.sweater.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.selezneva.sweater.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUserName(String userName);
}
