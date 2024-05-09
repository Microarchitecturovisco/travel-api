package org.microarchitecturovisco.userservice.repositories;

import org.microarchitecturovisco.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // You can define custom query methods here if needed
}
