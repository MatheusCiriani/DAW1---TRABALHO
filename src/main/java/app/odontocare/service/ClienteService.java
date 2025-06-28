package app.odontocare.service;

import app.odontocare.model.Cliente;
import app.odontocare.repository.ClienteRepository;
import app.odontocare.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Date; // Para java.util.Date

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Cliente cadastrarCliente(Cliente cliente) {
        if (cliente.getLogin() != null && usuarioRepository.findByLogin(cliente.getLogin()).isPresent()) {
            throw new RuntimeException("Login já cadastrado.");
        }
        // getEmail() em Cliente (herdado de Usuario)
        if (cliente.getEmail() != null && usuarioRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        // getDataCriacao() e setDataCriacao() em Cliente (herdado de Usuario)
        if (cliente.getDataCriacao() == null) {
            cliente.setDataCriacao(new Date());
        }

        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        // Agora que Cliente herda de Usuario, o findByEmail pode ser no UsuarioRepository
        // ou um método findByEmail no ClienteRepository que "busca" no campo email herdado.
        // Se o findByEmail em ClienteRepository espera um campo 'email' em Cliente, ele deve existir lá.
        // Se não existir, você precisará mudar para usuarioRepository.findByEmail(email)
        return clienteRepository.findByEmail(email);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional
    public Cliente atualizarPerfil(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    // getNomeCliente(), getEndereco(), getIdade(), getTelefone() em Cliente
                    clienteExistente.setNomeCliente(clienteAtualizado.getNomeCliente());
                    clienteExistente.setEndereco(clienteAtualizado.getEndereco());
                    clienteExistente.setIdade(clienteAtualizado.getIdade());
                    clienteExistente.setTelefone(clienteAtualizado.getTelefone());

                    // getEmail() e setEmail() em Cliente (herdado de Usuario)
                    if (!clienteExistente.getEmail().equals(clienteAtualizado.getEmail())) {
                        if (usuarioRepository.findByEmail(clienteAtualizado.getEmail()).isPresent()) {
                            throw new RuntimeException("Novo email já está em uso.");
                        }
                        clienteExistente.setEmail(clienteAtualizado.getEmail());
                    }

                    // getLogin() e setLogin() em Cliente (herdado de Usuario)
                    if (!clienteExistente.getLogin().equals(clienteAtualizado.getLogin())) {
                        if (usuarioRepository.findByLogin(clienteAtualizado.getLogin()).isPresent()) {
                            throw new RuntimeException("Novo login já está em uso.");
                        }
                        clienteExistente.setLogin(clienteAtualizado.getLogin());
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