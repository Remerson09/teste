package aula2603.controller;


import aula2603.model.entity.Paciente;
import aula2603.repository.ConsultaRepository;
import aula2603.repository.PacienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pacientes", pacienteRepository.findAll());
        return "paciente/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "paciente/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("paciente") Paciente paciente,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (result.hasErrors()) {
            return "paciente/form";
        }

        pacienteRepository.save(paciente);
        redirectAttributes.addFlashAttribute("success", "Paciente cadastrado com sucesso!");
        return "redirect:/pacientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + id));
        model.addAttribute("paciente", paciente);
        return "paciente/form";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("paciente") Paciente pacienteAtualizado,
                            BindingResult result,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        if (result.hasErrors()) {
            pacienteAtualizado.setId(id);
            return "paciente/form";
        }

        pacienteAtualizado.setId(id);
        pacienteRepository.save(pacienteAtualizado);
        redirectAttributes.addFlashAttribute("success", "Paciente atualizado com sucesso!");
        return "redirect:/pacientes";
    }

    @GetMapping("/excluir/{id}")
    public String excluirPaciente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (!pacienteRepository.existsById(id)) {
                throw new IllegalArgumentException("Paciente não encontrado para exclusão: " + id);
            }

            // Remove consultas vinculadas
            List<Consulta> consultas = consultaRepository.findByPacienteId(id);
            consultaRepository.deleteAll(consultas);

            // Remove paciente
            pacienteRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Paciente excluído com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir paciente: " + e.getMessage());
        }

        return "redirect:/pacientes";
    }

    @GetMapping("/consultas/{id}")
    public String consultasPaciente(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + id));

        List<Consulta> consultas = consultaRepository.findByPacienteIdWithMedico(id);

        model.addAttribute("paciente", paciente);
        model.addAttribute("consultas", consultas);
        return "paciente/consulta";
    }

    @GetMapping("/buscar")
    public String buscarPorNome(@RequestParam(required = false) String nome, Model model) {
        List<Paciente> pacientes;
        if (nome == null || nome.isBlank()) {
            pacientes = pacienteRepository.findAll();
        } else {
            pacientes = pacienteRepository.findByNomeContainingIgnoreCase(nome);
        }
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("nome", nome);
        return "paciente/list";
    }
}
