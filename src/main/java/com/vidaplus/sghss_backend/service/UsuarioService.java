package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Carregar usuário pelo email (username) para autenticação
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
    }

    /**
     * Buscar usuário pelo email
     */
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com email: " + email));
    }

    /**
     * Criar novo usuário
     */
    public Usuario criarUsuario(Usuario usuario) {
        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("Email já cadastrado."); });
        return usuarioRepository.save(usuario);
    }

    /**
     * Atualizar usuário
     */
    public Usuario atualizarUsuario(Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarPorEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setSenhaHash(usuarioAtualizado.getSenhaHash());
        usuarioExistente.setPerfil(usuarioAtualizado.getPerfil());
        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Deletar usuário
     */
    public void deletarUsuario(String email) {
        Usuario usuarioExistente = buscarPorEmail(email);
        usuarioRepository.delete(usuarioExistente);
    }
}
