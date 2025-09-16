package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Hospital;
import com.vidaplus.sghss_backend.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public Hospital salvar(Hospital hospital) {
        if (hospitalRepository.existsByCnpj(hospital.getCnpj())) {
            throw new RuntimeException("Já existe um hospital cadastrado com este CNPJ.");
        }
        return hospitalRepository.save(hospital);
    }

    public List<Hospital> listarTodos() {
        return hospitalRepository.findAll();
    }

    public Optional<Hospital> buscarPorId(UUID id) {
        return hospitalRepository.findById(id);
    }

    public void deletar(UUID id) {
        hospitalRepository.deleteById(id);
    }
}
