package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * Listar todos usuários
     * Apenas ADMIN pode chamar via controller
     */
    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Criar novo usuário
     * Regras:
     * - ADMIN pode criar qualquer perfil
     * - MEDICO só pode criar PACIENTE
     */
    public Usuario criarUsuario(Usuario usuario, Usuario usuarioLogado) {
        // Verifica se email já existe
        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("Email já cadastrado."); });

        // Regras de permissão
        if ("MEDICO".equals(usuarioLogado.getPerfil()) && !"PACIENTE".equals(usuario.getPerfil())) {
            throw new SecurityException("Médicos só podem criar usuários com perfil PACIENTE.");
        }

        if ("PACIENTE".equals(usuarioLogado.getPerfil())) {
            throw new SecurityException("Pacientes não podem criar usuários.");
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Criar usuário público (registro)
     * Sempre com perfil PACIENTE
     */
    public Usuario criarUsuarioPublico(Usuario usuario) {
        // Verifica se email já existe
        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("Email já cadastrado."); });

        // Define perfil PACIENTE
        usuario.setPerfil("PACIENTE");

        return usuarioRepository.save(usuario);
    }

    /**
     * Atualizar usuário
     * Regras:
     * - ADMIN pode atualizar qualquer usuário
     * - MEDICO só pode atualizar PACIENTE
     * - PACIENTE só pode atualizar ele mesmo
     */
    public Usuario atualizarUsuario(Usuario usuarioAtualizado, Usuario usuarioLogado) {
        Usuario usuarioExistente = buscarPorEmail(usuarioAtualizado.getEmail());

        if ("MEDICO".equals(usuarioLogado.getPerfil()) && !"PACIENTE".equals(usuarioExistente.getPerfil())) {
            throw new SecurityException("Médicos só podem atualizar usuários com perfil PACIENTE.");
        }

        if ("PACIENTE".equals(usuarioLogado.getPerfil())
                && !usuarioLogado.getEmail().equals(usuarioExistente.getEmail())) {
            throw new SecurityException("Pacientes só podem atualizar seus próprios dados.");
        }

        usuarioExistente.setSenhaHash(usuarioAtualizado.getSenhaHash());
        usuarioExistente.setPerfil(usuarioAtualizado.getPerfil());

        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Deletar usuário
     * Apenas ADMIN pode deletar
     */
    public void deletarUsuario(String email, Usuario usuarioLogado) {
        if (!"ADMIN".equals(usuarioLogado.getPerfil())) {
            throw new SecurityException("Apenas administradores podem deletar usuários.");
        }

        Usuario usuarioExistente = buscarPorEmail(email);
        usuarioRepository.delete(usuarioExistente);
    }
}
