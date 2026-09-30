package me.scpark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface MenberRepository extends JpaRepository<Menber,Long> {



}
