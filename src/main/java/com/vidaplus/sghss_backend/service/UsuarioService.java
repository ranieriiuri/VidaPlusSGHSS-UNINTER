package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.dto.UsuarioRequest;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final AuditLogService auditLogService; // ← audit logs
    private final PasswordEncoder passwordEncoder;

    public Usuario criarUsuario(UsuarioRequest request, Usuario usuarioLogado) {

        // Verifica se email já existe
        usuarioRepository.findByEmail(request.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("Email já cadastrado."); });

        // Regras de perfil
        if (PerfilUsuario.MEDICO.equals(usuarioLogado.getPerfil()) && !PerfilUsuario.PACIENTE.equals(PerfilUsuario.valueOf(request.getPerfil().name()))) {
            throw new SecurityException("Médicos só podem criar usuários com perfil PACIENTE.");
        }
        if (PerfilUsuario.PACIENTE.equals(usuarioLogado.getPerfil())) {
            throw new SecurityException("Pacientes não podem criar usuários.");
        }

        // Cria usuário
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .perfil(PerfilUsuario.valueOf(request.getPerfil().name()))
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Registrar log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "CRIAR_USUARIO",
                "Usuario",
                usuarioSalvo.getId(),
                "Email: " + usuarioSalvo.getEmail() + ", Perfil: " + usuarioSalvo.getPerfil()
        );

        return usuarioSalvo;
    }

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

        usuarioExistente.setSenhaHash(usuarioAtualizado.getSenhaHash());
        usuarioExistente.setPerfil(usuarioAtualizado.getPerfil());

        Usuario usuarioSalvo = usuarioRepository.save(usuarioExistente);

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "ATUALIZAR_USUARIO",
                "Usuario",
                usuarioSalvo.getId(),
                "Email: " + usuarioSalvo.getEmail() + ", Perfil: " + usuarioSalvo.getPerfil()
        );

        return usuarioSalvo;
    }

    public void deletarUsuario(String email, Usuario usuarioLogado) {
        if (!PerfilUsuario.ADMIN.equals(usuarioLogado.getPerfil())) {
            throw new SecurityException("Apenas administradores podem deletar usuários.");
        }

        Usuario usuarioExistente = buscarPorEmail(email);
        usuarioRepository.delete(usuarioExistente);

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "DELETAR_USUARIO",
                "Usuario",
                usuarioExistente.getId(),
                "Email: " + usuarioExistente.getEmail()
        );
    }
}
