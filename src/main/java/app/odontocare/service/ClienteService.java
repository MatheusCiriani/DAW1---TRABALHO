package app.odontocare.service;

import app.odontocare.model.Cliente;
import app.odontocare.model.Papel;
import app.odontocare.repository.ClienteRepository;
import app.odontocare.repository.UsuarioRepository;
import app.odontocare.repository.PapelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.HashSet;
import java.util.Collection;
import java.util.stream.Collectors;


@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PapelRepository papelRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          PapelRepository papelRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.papelRepository = papelRepository;
    }

    @Transactional
    public Cliente cadastrarCliente(Cliente cliente) {
        if (cliente.getLogin() != null && usuarioRepository.findByLogin(cliente.getLogin()).isPresent()) {
            throw new RuntimeException("Login já cadastrado.");
        }
        if (cliente.getEmail() != null && usuarioRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        if (cliente.getDataCriacao() == null) {
            cliente.setDataCriacao(new Date());
        }

        if (cliente.getSenha() == null || cliente.getSenha().isEmpty()) {
            throw new RuntimeException("A senha não pode ser vazia.");
        }
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));

        Papel papelCliente = papelRepository.findByNome("ROLE_CLIENTE")
            .orElseThrow(() -> new RuntimeException("Papel ROLE_CLIENTE não encontrado. Verifique as migrações Flyway."));
        cliente.getPapeis().add(papelCliente);
        cliente.setAtivo(true); // Definir como ativo por padrão

        return clienteRepository.save(cliente);
    }

    // MÉTODO RENOMEADO
    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    @Transactional
    public Cliente atualizarPerfil(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    clienteExistente.setNomeCliente(clienteAtualizado.getNomeCliente());
                    clienteExistente.setEndereco(clienteAtualizado.getEndereco());
                    clienteExistente.setIdade(clienteAtualizado.getIdade());
                    clienteExistente.setTelefone(clienteAtualizado.getTelefone());

                    if (!clienteExistente.getEmail().equals(clienteAtualizado.getEmail())) {
                        if (usuarioRepository.findByEmail(clienteAtualizado.getEmail()).isPresent()) {
                            throw new RuntimeException("Novo email já está em uso.");
                        }
                        clienteExistente.setEmail(clienteAtualizado.getEmail());
                    }

                    if (!clienteExistente.getLogin().equals(clienteAtualizado.getLogin())) {
                        if (usuarioRepository.findByLogin(clienteAtualizado.getLogin()).isPresent()) {
                            throw new RuntimeException("Novo login já está em uso.");
                        }
                        clienteExistente.setLogin(clienteAtualizado.getLogin());
                    }

                    if (clienteAtualizado.getSenha() != null && !clienteAtualizado.getSenha().isEmpty()) {
                         clienteExistente.setSenha(passwordEncoder.encode(clienteAtualizado.getSenha()));
                    }

                    return clienteRepository.save(clienteExistente);
                }).orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));
    }

    @Transactional
    public void deletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}