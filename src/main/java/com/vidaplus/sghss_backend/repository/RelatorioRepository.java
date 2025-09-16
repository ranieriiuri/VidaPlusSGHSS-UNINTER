package com.vidaplus.sghss_backend.repository;

import com.vidaplus.sghss_backend.model.Relatorio;
import com.vidaplus.sghss_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {
    List<Relatorio> findByGeradoPor(Usuario usuario);
}
