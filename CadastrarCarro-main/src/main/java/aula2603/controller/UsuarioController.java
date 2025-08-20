package aula2603.controller;

import aula2603.model.entity.Medico;
import aula2603.model.entity.Paciente;
import aula2603.model.entity.Usuario;
import aula2603.repository.MedicoRepository;
import aula2603.repository.PacienteRepository;
import aula2603.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             MedicoRepository medicoRepository,
                             PacienteRepository pacienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // --- LISTA DE USUÁRIOS (apenas admin) ---
    @GetMapping("/usuarios/list")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/list";
    }

    // --- CADASTRO DE NOVO USUÁRIO ---
    @GetMapping("/usuarios/form")
    public String novoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form"; // sua página de cadastro
    }

    // Redireciona /usuarios/new para /usuarios/form
    @GetMapping("/usuarios/new")
    public String redirecionarNovoUsuario() {
        return "redirect:/usuarios/form";
    }

    @PostMapping("/usuarios/novo")
    public String salvarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        usuario.setId(null); // força novo cadastro
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha())); // criptografa senha
        usuarioRepository.save(usuario);
        redirectAttributes.addFlashAttribute("success", "Usuário cadastrado com sucesso!");
        return "redirect:/usuarios/list";
    }

    // --- TORNAR MÉDICO ---
    @GetMapping("/usuarios/setMedico/{id}")
    public String setMedico(@PathVariable Long id) {
        if (id == 1) { // ID 1 é o administrador
            return "redirect:/usuarios/list"; // não faz nada
        }

        Usuario usuario = usuarioRepository.findById(id).orElseThrow();

        Medico medico = new Medico();
        medico.setNome(usuario.getNome());
        medico.setCrm("CRM-123");       // obrigatório e válido
        medico.setEspecialidade("Geral");

        usuario.setMedico(medico);
        usuarioRepository.save(usuario);

        return "redirect:/usuarios/list";
    }

    // --- TORNAR PACIENTE ---
    @GetMapping("/usuarios/setPaciente/{id}")
    public String setPaciente(@PathVariable Long id) {
        if (id == 1) { // ID 1 é o administrador
            return "redirect:/usuarios/list"; // não faz nada
        }

        Usuario usuario = usuarioRepository.findById(id).orElseThrow();

        Paciente paciente = new Paciente();
        paciente.setNome(usuario.getNome());
        paciente.setTelefone("(63)99999-9999"); // obrigatório, entre 10 e 15 caracteres

        usuario.setPaciente(paciente);
        usuarioRepository.save(usuario);

        return "redirect:/usuarios/list";
    }
}
