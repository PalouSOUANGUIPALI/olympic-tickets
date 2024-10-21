package com.infoevent.olympictickets.repository;

import com.infoevent.olympictickets.entity.Validation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValidationRepository extends CrudRepository<Validation, Integer> {

    Optional<Validation> findByCode(String code);

    @Modifying
    @Query("UPDATE Validation v SET v.user = NULL WHERE v.user.id = :id")
    void updateUserIdToNull(@Param("id") Integer id);
}