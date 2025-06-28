package app.odontocare.service;

import app.odontocare.model.Usuario;
import app.odontocare.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario criarUsuario(Usuario usuario) {
        // getLogin(), getEmail(), getDataCriacao() em Usuario
        if (usuario.getLogin() != null && usuarioRepository.findByLogin(usuario.getLogin()).isPresent()) {
            throw new RuntimeException("Login já existe!");
        }
        if (usuario.getEmail() != null && usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já existe!");
        }
        if (usuario.getDataCriacao() == null) {
            usuario.setDataCriacao(new Date()); // setDataCriacao() em Usuario
        }
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean emailOuLoginExiste(String identificador) {
        return usuarioRepository.findByLogin(identificador).isPresent() ||
               usuarioRepository.findByEmail(identificador).isPresent();
    }
}