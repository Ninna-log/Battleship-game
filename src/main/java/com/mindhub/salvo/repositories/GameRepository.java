package com.mindhub.salvo.repositories;

import com.mindhub.salvo.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Long> {

    // JpaRepository inherits from GameRepository

}