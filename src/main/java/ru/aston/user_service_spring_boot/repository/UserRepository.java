package ru.aston.user_service_spring_boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.aston.user_service_spring_boot.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
