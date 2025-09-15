package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Buscar usuário pelo email
     */
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com email: " + email));
    }

    /**
     * Futuro: criar usuário
     */
    public Usuario criarUsuario(Usuario usuario) {
        // Verifica se email já existe
        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("Email já cadastrado."); });

        return usuarioRepository.save(usuario);
    }

    /**
     * Futuro: atualizar usuário
     */
    public Usuario atualizarUsuario(Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarPorEmail(usuarioAtualizado.getEmail());

        usuarioExistente.setSenhaHash(usuarioAtualizado.getSenhaHash());
        usuarioExistente.setPerfil(usuarioAtualizado.getPerfil());

        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Futuro: deletar usuário
     */
    public void deletarUsuario(String email) {
        Usuario usuarioExistente = buscarPorEmail(email);
        usuarioRepository.delete(usuarioExistente);
    }
}
