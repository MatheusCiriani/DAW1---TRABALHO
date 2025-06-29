package app.odontocare.service;

import app.odontocare.model.Cliente;
import app.odontocare.model.Papel; // Importar Papel
import app.odontocare.repository.ClienteRepository;
import app.odontocare.repository.UsuarioRepository;
import app.odontocare.repository.PapelRepository; // Para buscar papéis
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder; // Para criptografia de senha

import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.HashSet; // Para Set de Papel

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
        // Validação de login único
        if (cliente.getLogin() != null && usuarioRepository.findByLogin(cliente.getLogin()).isPresent()) {
            throw new RuntimeException("Login já cadastrado.");
        }
        // Validação de email único
        if (cliente.getEmail() != null && usuarioRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        if (cliente.getDataCriacao() == null) {
            cliente.setDataCriacao(new Date());
        }

        // Criptografar a senha
        if (cliente.getSenha() == null || cliente.getSenha().isEmpty()) {
            throw new RuntimeException("A senha não pode ser vazia.");
        }
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));

        // Atribuir papel padrão de ROLE_CLIENTE
        // Garantir que o papel ROLE_CLIENTE exista no DB (inserido via Flyway V2)
        Papel papelCliente = papelRepository.findByNome("ROLE_CLIENTE")
            .orElseThrow(() -> new RuntimeException("Papel ROLE_CLIENTE não encontrado. Verifique as migrações Flyway."));
        cliente.getPapeis().add(papelCliente);
        cliente.setAtivo(true); // Definir como ativo por padrão

        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        // Este método busca um cliente pelo email.
        // Se o email deve ser único entre todos os usuários (cliente/dentista),
        // o findByEmail deveria ser no UsuarioRepository para verificar a unicidade global.
        return clienteRepository.findByEmail(email);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional
    public Cliente atualizarPerfil(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    // Atualizar campos específicos de Cliente
                    clienteExistente.setNomeCliente(clienteAtualizado.getNomeCliente());
                    clienteExistente.setEndereco(clienteAtualizado.getEndereco());
                    clienteExistente.setIdade(clienteAtualizado.getIdade());
                    clienteExistente.setTelefone(clienteAtualizado.getTelefone());

                    // Atualizar email (herdado de Usuario), com validação de unicidade global
                    if (!clienteExistente.getEmail().equals(clienteAtualizado.getEmail())) {
                        if (usuarioRepository.findByEmail(clienteAtualizado.getEmail()).isPresent()) {
                            throw new RuntimeException("Novo email já está em uso.");
                        }
                        clienteExistente.setEmail(clienteAtualizado.getEmail());
                    }

                    // Atualizar login (herdado de Usuario), com validação de unicidade global
                    if (!clienteExistente.getLogin().equals(clienteAtualizado.getLogin())) {
                        if (usuarioRepository.findByLogin(clienteAtualizado.getLogin()).isPresent()) {
                            throw new RuntimeException("Novo login já está em uso.");
                        }
                        clienteExistente.setLogin(clienteAtualizado.getLogin());
                    }

                    // Atualizar senha APENAS se uma nova senha for fornecida no formulário
                    // e criptografar a nova senha.
                    if (clienteAtualizado.getSenha() != null && !clienteAtualizado.getSenha().isEmpty()) {
                         clienteExistente.setSenha(passwordEncoder.encode(clienteAtualizado.getSenha()));
                    }
                    // A coluna 'ativo' pode ser atualizada aqui se houver um controle na interface,
                    // mas geralmente é gerenciada por administradores e não pelo próprio usuário.
                    // clienteExistente.setAtivo(clienteAtualizado.isAtivo());

                    return clienteRepository.save(clienteExistente);
                }).orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));
    }

    @Transactional
    public void deletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com id: " + id);
        }
        // A deleção do cliente (que é um usuário) deve remover o registro na tabela 'clientes' e 'usuarios'.
        // Isso é gerenciado pelo CascadeType.ALL nas entidades se configurado corretamente.
        clienteRepository.deleteById(id);
    }
}