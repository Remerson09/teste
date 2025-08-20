package aula2603.controller;

import aula2603.model.entity.*;
import aula2603.repository.AgendaRepository;
import aula2603.repository.ConsultaRepository;
import aula2603.repository.MedicoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private AgendaRepository agendaRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private ConsultaRepository consultaRepository;

    // --- LISTAGEM ---
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicos", medicoRepository.findAll());
        return "medico/list";
    }

    // --- NOVO MÉDICO ---
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("medico", new Medico());
        return "medico/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("medico") Medico medico,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        // Validar CRM usando o validador customizado
        String crmNumero = CrmValidator.extractCrmNumber(medico.getCrm());
        if (crmNumero == null) {
            result.rejectValue("crm", "error.medico.crm.invalid", "Digite apenas números (ex: 12345)");
        } else {
            medico.setCrm(crmNumero); // salvar apenas os números
        }

        // Verificar se CRM já existe
        if (crmNumero != null) {
            Medico existente = medicoRepository.findByCrm(crmNumero);
            if (existente != null) {
                result.rejectValue("crm", "error.medico.crm.exists", "Este CRM já está cadastrado no sistema");
            }
        }

        if (result.hasErrors()) {
            return "medico/form";
        }

        medicoRepository.save(medico);
        redirectAttributes.addFlashAttribute("success", "Médico cadastrado com sucesso!");
            return "redirect:/medicos";
    }


    // --- EDITAR MÉDICO ---
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado: " + id));
        model.addAttribute("medico", medico);
        return "medico/form";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("medico") Medico medicoAtualizado,
                            BindingResult result,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        // Remove espaços e mantém apenas números
        if (medicoAtualizado.getCrm() != null) {
            medicoAtualizado.setCrm(medicoAtualizado.getCrm().trim().replaceAll("\\D", ""));
        }

        // Verifica CRM duplicado, exceto para o próprio médico
        if (medicoAtualizado.getCrm() != null && !medicoAtualizado.getCrm().isEmpty()) {
            Medico existente = medicoRepository.findByCrm(medicoAtualizado.getCrm());
            if (existente != null && !existente.getId().equals(id)) {
                result.rejectValue("crm", "error.medico.crm.exists", "Este CRM já está cadastrado no sistema");
            }
        }

        if (result.hasErrors()) {
            medicoAtualizado.setId(id);
            return "medico/form";
        }

        try {
            medicoAtualizado.setId(id);
            medicoRepository.save(medicoAtualizado);
            redirectAttributes.addFlashAttribute("success", "Médico atualizado com sucesso!");
            return "redirect:/medicos";
        } catch (Exception e) {
            medicoAtualizado.setId(id);
            model.addAttribute("error", "Erro inesperado ao atualizar: " + e.getMessage());
            return "medico/form";
        }
    }

    // --- EXCLUIR ---
    @GetMapping("/excluir/{id}")
    public String excluirMedico(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (!medicoRepository.existsById(id)) {
                throw new IllegalArgumentException("Médico não encontrado para exclusão: " + id);
            }

            long consultasCount = consultaRepository.countByMedicoId(id);
            if (consultasCount > 0) {
                redirectAttributes.addFlashAttribute("error",
                        "Não é possível excluir o médico pois ele possui consultas cadastradas.");
                return "redirect:/medicos";
            }

            medicoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Médico excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir médico: " + e.getMessage());
        }
        return "redirect:/medicos";
    }

    // --- BUSCA ---
    @GetMapping("/buscar")
    public String buscarMedicos(@RequestParam(required = false) String nome,
                                @RequestParam(required = false) String crm,
                                Model model) {
        List<Medico> medicos;

        if ((nome == null || nome.isBlank()) && (crm == null || crm.isBlank())) {
            medicos = medicoRepository.findAll();
        } else if (crm != null && !crm.isBlank()) {
            medicos = medicoRepository.findByCrmContainingIgnoreCase(crm.replaceAll("\\D", ""));
        } else {
            medicos = medicoRepository.findByNomeContainingIgnoreCase(nome.trim());
        }

        model.addAttribute("medicos", medicos);
        model.addAttribute("nome", nome);
        model.addAttribute("crm", crm);
        return "medico/list";
    }

    // --- VALIDAR CRM VIA AJAX ---
    @GetMapping("/validar-crm")
    @ResponseBody
    public String validarCrm(@RequestParam String crm, @RequestParam(required = false) Long id) {
        if (crm == null || crm.trim().isEmpty()) {
            return "CRM é obrigatório";
        }

        String crmNumeros = crm.trim().replaceAll("\\D", "");

        if (!crmNumeros.matches("\\d{5}")) {
            return "Digite apenas números (ex: 12345)";
        }

        Medico existente = medicoRepository.findByCrm(crmNumeros);
        if (existente != null && (id == null || !existente.getId().equals(id))) {
            return "Este CRM já está cadastrado";
        }

        return "OK";
    }

    // --- AGENDAS ---
    @GetMapping("/{id}/agenda")
    public String verAgendaDoMedico(@PathVariable Long id, Model model) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico não encontrado: " + id));

        List<Agenda> agendaDoMedico = agendaRepository.findByMedico(medico);

        model.addAttribute("medico", medico);
        model.addAttribute("agendaDoMedico", agendaDoMedico);
        return "medico/agenda";
    }
}
