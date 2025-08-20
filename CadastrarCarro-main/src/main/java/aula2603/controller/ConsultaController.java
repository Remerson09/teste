package aula2603.controller;

import aula2603.model.entity.Agenda;
import aula2603.model.entity.AgendaStatus;
import aula2603.model.entity.Consulta;
import aula2603.model.entity.Paciente;
import aula2603.repository.AgendaRepository;
import aula2603.repository.ConsultaRepository;
import aula2603.repository.MedicoRepository;
import aula2603.repository.PacienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    // -------------------------------
    // LISTAR CONSULTAS
    // -------------------------------
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("consultas", consultaRepository.findAllWithPacienteAndMedico());
        return "consulta/list";
    }

    // -------------------------------
    // NOVA CONSULTA
    // -------------------------------
    @GetMapping("/nova")
    public String novoForm(Model model) {
        prepararForm(model, new Consulta());
        return "consulta/form";
    }

    @GetMapping("/nova/{id}")
    public String novoFormComPaciente(@PathVariable Long id, Model model) {
        Consulta consulta = new Consulta();
        consulta.setPaciente(pacienteRepository.findById(id).orElseThrow());
        prepararForm(model, consulta);
        return "consulta/form";
    }

    private void prepararForm(Model model, Consulta consulta) {
        model.addAttribute("consulta", consulta);
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAllByOrderByNomeAsc());
    }

    // -------------------------------
    // SALVAR CONSULTA
    // -------------------------------
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Consulta consulta,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (result.hasErrors()) {
            prepararForm(model, consulta);
            return "consulta/form";
        }

        try {
            if (consulta.getPaciente() == null || consulta.getPaciente().getId() == null) {
                throw new IllegalArgumentException("Paciente não informado");
            }

            if (consulta.getMedico() == null || consulta.getMedico().getId() == null) {
                throw new IllegalArgumentException("Médico não selecionado");
            }

            if (consulta.getData() == null) {
                throw new IllegalArgumentException("Data e hora da consulta não informadas");
            }

            // VALIDAÇÃO: intervalo mínimo de 30 minutos entre consultas
            LocalDateTime dataConsulta = consulta.getData();
            LocalDateTime intervaloInicio = dataConsulta.minusMinutes(30);
            LocalDateTime intervaloFim = dataConsulta.plusMinutes(30);

            boolean consultaMuitoProxima = consultaRepository.existsByMedicoAndDataBetween(
                    consulta.getMedico(),
                    intervaloInicio,
                    intervaloFim);

            if (consultaMuitoProxima) {
                result.rejectValue("data", null, "Já existe uma consulta agendada para este médico com intervalo inferior a 30 minutos.");
                prepararForm(model, consulta);
                return "consulta/form";
            }

            // Buscar agenda disponível
            List<Agenda> agendasDisponiveis = agendaRepository.findDisponibilidadeNoMomento(
                    consulta.getMedico(),
                    consulta.getData());

            if (agendasDisponiveis.isEmpty()) {
                result.rejectValue("data", null, "Médico não está disponível neste horário.");
                prepararForm(model, consulta);
                return "consulta/form";
            }

            Agenda agenda = agendasDisponiveis.get(0);
            agenda.setStatus(AgendaStatus.AGENDADO);
            consulta.setAgenda(agenda);
            agenda.setConsulta(consulta);

            // Salva consulta e agenda
            consultaRepository.save(consulta);
            agendaRepository.save(agenda);

            redirectAttributes.addFlashAttribute("success", "Consulta agendada com sucesso");
            return "redirect:/pacientes/consultas/" + consulta.getPaciente().getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao agendar consulta: " + e.getMessage());
            return "redirect:/consultas/nova/" + (consulta.getPaciente() != null ? consulta.getPaciente().getId() : "");
        }
    }

    // -------------------------------
    // EDITAR CONSULTA
    // -------------------------------
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Consulta consulta = consultaRepository.findByIdWithPacienteAndMedico(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));
        prepararForm(model, consulta);
        return "consulta/editar";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Long id, @ModelAttribute Consulta consulta,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            prepararForm(model, consulta);
            return "consulta/editar";
        }

        consulta.setId(id);
        consultaRepository.save(consulta);
        return "redirect:/pacientes/consultas/" + consulta.getPaciente().getId();
    }

    // -------------------------------
    // EXCLUIR CONSULTA
    // -------------------------------
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Consulta consulta = consultaRepository.findByIdWithPacienteAndMedico(id)
                    .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));
            Long pacienteId = consulta.getPaciente().getId();
            consultaRepository.delete(consulta);
            redirectAttributes.addFlashAttribute("success", "Consulta excluída com sucesso");
            return "redirect:/pacientes/consultas/" + pacienteId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir consulta: " + e.getMessage());
            return "redirect:/consultas";
        }
    }

    // -------------------------------
    // BUSCAR POR DATA
    // -------------------------------
    @GetMapping("/buscar")
    public String buscarPorData(@RequestParam("data") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data,
                                Model model) {
        try {
            List<Consulta> consultas = consultaRepository.findByData(data);
            List<Paciente> pacientesDaData = consultaRepository.findPacientesByData(data);

            model.addAttribute("consultas", consultas);
            model.addAttribute("dataSelecionada", data);
            model.addAttribute("pacientesDaData", pacientesDaData);

            return "consulta/list";

        } catch (Exception e) {
            model.addAttribute("error", "Erro ao buscar consultas: " + e.getMessage());
            return "consulta/list";
        }
    }

    // -------------------------------
    // BUSCAR POR PERÍODO
    // -------------------------------
    @GetMapping("/buscar-por-periodo")
    public String buscarPorPeriodo(@RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                   @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                                   Model model) {

        List<Consulta> consultas = consultaRepository.findByDataBetween(
                dataInicio.atStartOfDay(),
                dataFim.plusDays(1).atStartOfDay()
        );

        List<Paciente> pacientesDoPeriodo = consultas.stream()
                .map(Consulta::getPaciente)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("consultas", consultas);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("pacientesDoPeriodo", pacientesDoPeriodo);

        return "consulta/list";
    }
}