package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com email: " + email));
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario criarUsuario(Usuario usuario, Usuario usuarioLogado) {
        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("Email já cadastrado."); });

        // Regras usando PerfilUsuario
        if (PerfilUsuario.MEDICO.equals(usuarioLogado.getPerfil()) && !PerfilUsuario.PACIENTE.equals(usuario.getPerfil())) {
            throw new SecurityException("Médicos só podem criar usuários com perfil PACIENTE.");
        }
        if (PerfilUsuario.PACIENTE.equals(usuarioLogado.getPerfil())) {
            throw new SecurityException("Pacientes não podem criar usuários.");
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario criarUsuarioPublico(Usuario usuario) {
        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("Email já cadastrado."); });

        usuario.setPerfil(PerfilUsuario.PACIENTE);
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(Usuario usuarioAtualizado, Usuario usuarioLogado) {
        Usuario usuarioExistente = buscarPorEmail(usuarioAtualizado.getEmail());

        if (PerfilUsuario.MEDICO.equals(usuarioLogado.getPerfil()) && !PerfilUsuario.PACIENTE.equals(usuarioExistente.getPerfil())) {
            throw new SecurityException("Médicos só podem atualizar usuários com perfil PACIENTE.");
        }

        if (PerfilUsuario.PACIENTE.equals(usuarioLogado.getPerfil()) &&
                !usuarioLogado.getEmail().equals(usuarioExistente.getEmail())) {
            throw new SecurityException("Pacientes só podem atualizar seus próprios dados.");
        }

        // Apenas senha e perfil podem ser atualizados
        usuarioExistente.setSenhaHash(usuarioAtualizado.getSenhaHash());
        usuarioExistente.setPerfil(usuarioAtualizado.getPerfil());

        return usuarioRepository.save(usuarioExistente);
    }

    public void deletarUsuario(String email, Usuario usuarioLogado) {
        if (!PerfilUsuario.ADMIN.equals(usuarioLogado.getPerfil())) {
            throw new SecurityException("Apenas administradores podem deletar usuários.");
        }

        Usuario usuarioExistente = buscarPorEmail(email);
        usuarioRepository.delete(usuarioExistente);
    }
}
