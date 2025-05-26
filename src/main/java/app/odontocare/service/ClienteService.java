package odontocare.service;

import odontocare.model.Cliente;
import odontocare.model.Usuario; // Adicionado
import odontocare.repository.ClienteRepository;
import odontocare.repository.UsuarioRepository; // Adicionado
// import org.springframework.security.crypto.password.PasswordEncoder; // Para senhas
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository; // Para gerenciar o usuário associado
    // private final PasswordEncoder passwordEncoder; // Injete se for usar Spring Security

    @Autowired
    public ClienteService(ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository
                          /*PasswordEncoder passwordEncoder*/) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        // this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Cliente cadastrarCliente(Cliente cliente) {
        // Validação de email único para cliente
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new RuntimeException("Email do cliente já cadastrado");
        }

        Usuario usuario = cliente.getUsuario();
        if (usuario != null) {
            // Validação de login único para usuário
            if (usuario.getLogin() != null && usuarioRepository.findByLogin(usuario.getLogin()).isPresent()) {
                throw new RuntimeException("Login de usuário já cadastrado");
            }
            // Exemplo de hash de senha (DESCOMENTE e ajuste se usar Spring Security)
            // if (usuario.getSenha() != null) {
            //     usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            // }
            // Se o usuário for novo e gerenciado aqui
            // usuarioRepository.save(usuario); // JPA fará cascade se configurado em Cliente->Usuario
        }
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
                    if (!clienteExistente.getEmail().equals(clienteAtualizado.getEmail()) &&
                        clienteRepository.findByEmail(clienteAtualizado.getEmail()).isPresent()) {
                        throw new RuntimeException("Novo email já está em uso por outro cliente.");
                    }
                    clienteExistente.setEmail(clienteAtualizado.getEmail());
                    clienteExistente.setIdade(clienteAtualizado.getIdade());
                    clienteExistente.setTelefone(clienteAtualizado.getTelefone());

                    // Atualizar dados do usuário associado, se necessário e permitido
                    if (clienteAtualizado.getUsuario() != null && clienteExistente.getUsuario() != null) {
                        Usuario usuarioAtualizado = clienteAtualizado.getUsuario();
                        Usuario usuarioExistente = clienteExistente.getUsuario();

                        // Exemplo: Permitir atualização do login do usuário (com validação de unicidade)
                        // if (usuarioAtualizado.getLogin() != null && !usuarioAtualizado.getLogin().equals(usuarioExistente.getLogin())) {
                        //     if (usuarioRepository.findByLogin(usuarioAtualizado.getLogin()).isPresent()) {
                        //         throw new RuntimeException("Novo login de usuário já está em uso.");
                        //     }
                        //     usuarioExistente.setLogin(usuarioAtualizado.getLogin());
                        // }
                        // Nunca atualize a senha diretamente assim sem re-criptografar
                    }
                    return clienteRepository.save(clienteExistente);
                }).orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));
    }

    @Transactional
    public void deletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com id: " + id);
        }
        clienteRepository.deleteById(id); // Cascade deve remover o usuário associado se configurado
    }
}