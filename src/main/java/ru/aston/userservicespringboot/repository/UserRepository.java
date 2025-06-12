package ru.aston.userservicespringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.aston.userservicespringboot.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
