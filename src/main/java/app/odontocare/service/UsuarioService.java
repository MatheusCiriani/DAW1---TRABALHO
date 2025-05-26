package odontocare.service;

import odontocare.model.Usuario;
import odontocare.repository.UsuarioRepository;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    // private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository /*, PasswordEncoder passwordEncoder*/) {
        this.usuarioRepository = usuarioRepository;
        // this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario criarUsuario(Usuario usuario) {
        if (usuarioRepository.findByLogin(usuario.getLogin()).isPresent()) {
            throw new RuntimeException("Login já existe!");
        }
        // Lembre-se de criptografar a senha
        // usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }

    public boolean emailOuLoginExiste(String identificador) {
        // Este método é um exemplo simplificado para o htmx.
        // Em um caso real, você pode querer distinguir entre email do cliente/dentista e login do usuário.
        // Aqui, estamos assumindo que 'identificador' é um login de usuário.
        return usuarioRepository.findByLogin(identificador).isPresent();
    }

    // Adicionar métodos para autenticação (verifLogin), atualização de senha, etc.
}