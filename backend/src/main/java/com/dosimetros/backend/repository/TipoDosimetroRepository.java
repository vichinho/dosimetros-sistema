package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.TipoDosimetro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoDosimetroRepository extends JpaRepository<TipoDosimetro, Integer> {
}
