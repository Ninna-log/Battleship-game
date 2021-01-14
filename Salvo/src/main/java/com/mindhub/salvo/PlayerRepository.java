package com.mindhub.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> { // JpaRepository inherits from PlayerRepository
    public default void findByUserName(){

    }
}