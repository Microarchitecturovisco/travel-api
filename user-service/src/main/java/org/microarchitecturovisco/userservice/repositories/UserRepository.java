package org.microarchitecturovisco.userservice.repositories;

import org.microarchitecturovisco.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
