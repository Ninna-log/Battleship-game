package com.mindhub.salvo.repositories;

import com.mindhub.salvo.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> { // JpaRepository inherits from PlayerRepository

    Player findByUserName(@Param("userName")String userName);
}