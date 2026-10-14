package me.scpark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface MenberRepository extends JpaRepository<Menber,Long> {


    Optional<Menber> findByName(String c);
}
