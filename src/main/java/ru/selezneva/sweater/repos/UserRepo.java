package ru.selezneva.sweater.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.selezneva.sweater.domain.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUserName(String userName);
}
