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

    // Spring Security: Carrega usuário pelo email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
    }

    // Buscar usuário por email
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com email: " + email));
    }

    // Listar todos usuários
    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Criar usuário (ADMIN ou MEDICO)
    public Usuario criarUsuario(Usuario usuario, Usuario usuarioLogado) {
        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("Email já cadastrado."); });

        if ("MEDICO".equals(usuarioLogado.getPerfil()) && !"PACIENTE".equals(usuario.getPerfil())) {
            throw new SecurityException("Médicos só podem criar usuários com perfil PACIENTE.");
        }
        if ("PACIENTE".equals(usuarioLogado.getPerfil())) {
            throw new SecurityException("Pacientes não podem criar usuários.");
        }

        return usuarioRepository.save(usuario);
    }

    // Registro público - apenas PACIENTE
    public Usuario criarUsuarioPublico(Usuario usuario) {
        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("Email já cadastrado."); });

        usuario.setPerfil("PACIENTE");
        return usuarioRepository.save(usuario);
    }

    // Atualizar usuário
    public Usuario atualizarUsuario(Usuario usuarioAtualizado, Usuario usuarioLogado) {
        Usuario usuarioExistente = buscarPorEmail(usuarioAtualizado.getEmail());

        if ("MEDICO".equals(usuarioLogado.getPerfil()) && !"PACIENTE".equals(usuarioExistente.getPerfil())) {
            throw new SecurityException("Médicos só podem atualizar usuários com perfil PACIENTE.");
        }

        if ("PACIENTE".equals(usuarioLogado.getPerfil()) &&
                !usuarioLogado.getEmail().equals(usuarioExistente.getEmail())) {
            throw new SecurityException("Pacientes só podem atualizar seus próprios dados.");
        }

        // Apenas senha e perfil podem ser atualizados
        usuarioExistente.setSenhaHash(usuarioAtualizado.getSenhaHash());
        usuarioExistente.setPerfil(usuarioAtualizado.getPerfil());

        return usuarioRepository.save(usuarioExistente);
    }

    // Deletar usuário (apenas ADMIN)
    public void deletarUsuario(String email, Usuario usuarioLogado) {
        if (!"ADMIN".equals(usuarioLogado.getPerfil())) {
            throw new SecurityException("Apenas administradores podem deletar usuários.");
        }

        Usuario usuarioExistente = buscarPorEmail(email);
        usuarioRepository.delete(usuarioExistente);
    }
}
