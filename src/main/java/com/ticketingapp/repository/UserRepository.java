package com.ticketingapp.repository;

import com.ticketingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
    @Transactional
    void deleteByUsername(String username);
    List<User> findAllByRoleDescriptionIgnoreCase(String description);
}
