package com.infoevent.olympictickets.repository;

import com.infoevent.olympictickets.entity.Jwt;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface JwtRepository extends CrudRepository<Jwt, Integer> {
    Optional<Jwt> findByValeurAndDesactiveAndExpire(String valeur, boolean desactive, boolean expire);

    @Query("FROM Jwt j WHERE j.expire = :expire AND j.desactive = :desactive AND j.user.email = :email")
    Optional<Jwt> findUserValidToken(String email, boolean desactive, boolean expire);

    @Query("FROM Jwt j WHERE j.user.email = :email")
    Stream<Jwt> findUser(String email);

    @Query("FROM Jwt j WHERE j.refreshToken.valeur = :valeur")
    Optional<Jwt> findByRefreshToken(String valeur);

    //void deleteAllByExpireAndDesactive(boolean expire, boolean desactive);

    @Modifying
    @Query("UPDATE Jwt j SET j.user = NULL WHERE j.user.id = :id")
    void updateUserIdToNull(@Param("id") Integer id);


}