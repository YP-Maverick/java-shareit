package ru.practicum.shareit.user.storage;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("DELETE FROM User WHERE id = :id")
    void delete(Long id);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findById(@Param("id") Long id);
}