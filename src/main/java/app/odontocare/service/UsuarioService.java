package app.odontocare.service;

import app.odontocare.model.Usuario;
import app.odontocare.model.Papel; // Importar Papel
import app.odontocare.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetailsService; // NOVO: Importar
import org.springframework.security.core.userdetails.UserDetails; // NOVO: Importar
import org.springframework.security.core.userdetails.UsernameNotFoundException; // NOVO: Importar
import org.springframework.security.core.GrantedAuthority; // NOVO: Importar
import org.springframework.security.core.authority.SimpleGrantedAuthority; // NOVO: Importar

import java.util.Date;
import java.util.Optional;
import java.util.Collection; // NOVO: Importar
import java.util.stream.Collectors; // NOVO: Importar

@Service // Garante que é um componente Spring
@Transactional // Garante transações nos métodos
public class UsuarioService implements UserDetailsService { // NOVO: Implementa UserDetailsService

    private final UsuarioRepository usuarioRepository;
    // O PasswordEncoder será injetado e usado no Controller ou Service que cadastra o usuário.
    // Não é necessário injetá-lo aqui no UserDetailsService, pois este serviço apenas CARREGA usuários.

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario criarUsuario(Usuario usuario) {
        if (usuario.getLogin() != null && usuarioRepository.findByLogin(usuario.getLogin()).isPresent()) {
            throw new RuntimeException("Login já existe!");
        }
        if (usuario.getEmail() != null && usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já existe!");
        }
        if (usuario.getDataCriacao() == null) {
            usuario.setDataCriacao(new Date());
        }
        // A senha será criptografada no Controller ou em um serviço de cadastro específico antes de chamar o save.
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

    // NOVO: Implementação do método loadUserByUsername da interface UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Busca o usuário pelo login (nome de usuário)
        Usuario usuario = usuarioRepository.findByLogin(login)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + login));

        // Converte os papéis do seu modelo (Papel) para as GrantedAuthority do Spring Security
        Collection<? extends GrantedAuthority> authorities =
            usuario.getPapeis().stream()
                .map(papel -> new SimpleGrantedAuthority(papel.getNome())) // Papeis devem ser "ROLE_NOME"
                .collect(Collectors.toList());

        // Retorna um objeto UserDetails do Spring Security
        return new org.springframework.security.core.userdetails.User(
            usuario.getLogin(), // Username
            usuario.getSenha(), // Senha (já criptografada no DB)
            usuario.isAtivo(),  // Ativo/Inativo
            true, // Conta não expirada
            true, // Credenciais não expiradas
            true, // Conta não bloqueada
            authorities // Papéis/Autorizações
        );
    }
}