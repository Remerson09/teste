package aula2603.controller;

import aula2603.model.entity.Usuario;
import aula2603.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PerfilController {

    private final UsuarioRepository usuarioRepository;

    public PerfilController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal Usuario usuarioLogado, Model model) {
        // Usuario logado é injetado automaticamente pelo Spring Security
        model.addAttribute("usuario", usuarioLogado);
        return "user/perfil"; // nome do template: perfil.html em src/main/resources/templates
    }
}
