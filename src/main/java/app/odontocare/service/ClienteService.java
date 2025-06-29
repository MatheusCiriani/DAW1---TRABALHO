package app.odontocare.service;

import app.odontocare.model.Cliente;
import app.odontocare.model.Papel; // Adicionar se você atribuir papéis padrão aqui
import app.odontocare.repository.ClienteRepository;
import app.odontocare.repository.UsuarioRepository;
import app.odontocare.repository.PapelRepository; // NOVO: para buscar papéis
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder; // NOVO: Importar

import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.HashSet; // Para Set de Papel

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // NOVO: Injetar o PasswordEncoder
    private final PapelRepository papelRepository; // NOVO: Injetar PapelRepository para atribuir papel padrão

    @Autowired
    public ClienteService(ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder, // NOVO
                          PapelRepository papelRepository) { // NOVO
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder; // NOVO
        this.papelRepository = papelRepository; // NOVO
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

        // NOVO: Criptografar a senha antes de salvar
        if (cliente.getSenha() != null && !cliente.getSenha().isEmpty()) {
            cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        } else {
            throw new RuntimeException("A senha não pode ser vazia."); // Senha é obrigatória
        }

        // NOVO: Atribuir papel padrão de ROLE_CLIENTE
        Papel papelCliente = papelRepository.findByNome("ROLE_CLIENTE")
            .orElseThrow(() -> new RuntimeException("Papel ROLE_CLIENTE não encontrado. Execute as migrações Flyway."));
        cliente.getPapeis().add(papelCliente); // Adicionar o papel ao Set de papéis do cliente
        cliente.setAtivo(true); // NOVO: Definir como ativo por padrão

        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional
    public Cliente atualizarPerfil(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    clienteExistente.setNomeCliente(clienteAtualizado.getNomeCliente());
                    clienteExistente.setEndereco(clienteAtualizado.getEndereco());
                    clienteExistente.setIdade(clienteAtualizado.getIdade());
                    clienteExistente.setTelefone(clienteAtualizado.getTelefone());

                    // Atualizar email (herdado de Usuario)
                    if (!clienteExistente.getEmail().equals(clienteAtualizado.getEmail())) {
                        if (usuarioRepository.findByEmail(clienteAtualizado.getEmail()).isPresent()) {
                            throw new RuntimeException("Novo email já está em uso.");
                        }
                        clienteExistente.setEmail(clienteAtualizado.getEmail());
                    }

                    // Atualizar login (herdado de Usuario)
                    if (!clienteExistente.getLogin().equals(clienteAtualizado.getLogin())) {
                        if (usuarioRepository.findByLogin(clienteAtualizado.getLogin()).isPresent()) {
                            throw new RuntimeException("Novo login já está em uso.");
                        }
                        clienteExistente.setLogin(clienteAtualizado.getLogin());
                    }

                    // NOVO: Atualizar senha APENAS se uma nova senha for fornecida no formulário
                    // A senha deve ser criptografada.
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