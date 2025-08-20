package aula2603.controller;

import aula2603.model.entity.Usuario;
import aula2603.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    public AdminController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/admins")
    public String listarUsuarios(@AuthenticationPrincipal Usuario usuarioLogado, Model model) {
        // Lista todos os usuários do sistema
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Adiciona ao modelo os usuários e o usuário logado
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuarioLogado", usuarioLogado);

        // Retorna o template Thymeleaf: src/main/resources/templates/admin/admins.html
        return "admin/admins";
    }
}
