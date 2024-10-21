package com.infoevent.olympictickets.repository;

import com.infoevent.olympictickets.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query("FROM User u WHERE u.lastName LIKE '%lastName%' OR u.firstName LIKE '%lastName%' ")
    List<User> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String lastName, String firstName);

}
