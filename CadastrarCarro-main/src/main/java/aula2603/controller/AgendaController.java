package aula2603.controller;

import aula2603.model.entity.Agenda;
import aula2603.model.entity.AgendaStatus;
import aula2603.model.entity.Disponibilidade;
import aula2603.model.entity.Medico;
import aula2603.model.entity.StatusMedico;
import aula2603.repository.AgendaRepository;
import aula2603.repository.ConsultaRepository;
import aula2603.repository.DisponibilidadeRepository;
import aula2603.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/agendas")
public class AgendaController {

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    // -------------------------------
    // PESQUISA AVANÇADA DE AGENDAS
    // -------------------------------
    @GetMapping("/pesquisar")
    public String pesquisarAgendas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime horarioInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime horarioFim,
            @RequestParam(required = false) AgendaStatus statusSelecionado,
            @RequestParam(name = "medicoId", required = false) Long medicoId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (data == null) {
            data = LocalDate.now();
        }
        if (horarioInicio == null) {
            horarioInicio = LocalTime.of(8, 0);
        }
        if (horarioFim == null) {
            horarioFim = LocalTime.of(18, 0);
        }

        LocalDateTime dataHoraInicio = data.atTime(horarioInicio);
        LocalDateTime dataHoraFim = data.atTime(horarioFim);

        List<Agenda> agendas;

        try {
            boolean filtrarPorTodosMedicos = (medicoId == null);
            boolean filtrarPorTodosStatus = (statusSelecionado == null);

            if (filtrarPorTodosMedicos && filtrarPorTodosStatus) {
                agendas = agendaRepository.findByDataHoraInicioBetween(dataHoraInicio, dataHoraFim);
            } else if (filtrarPorTodosMedicos) {
                agendas = agendaRepository.findByStatusAndDataHoraInicioBetween(
                        statusSelecionado, dataHoraInicio, dataHoraFim);
            } else if (filtrarPorTodosStatus) {
                Medico medico = medicoRepository.findById(medicoId)
                        .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));
                agendas = agendaRepository.findByMedicoAndDataHoraInicioBetween(
                        medico, dataHoraInicio, dataHoraFim);
            } else {
                Medico medico = medicoRepository.findById(medicoId)
                        .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));
                agendas = agendaRepository.findByMedicoAndStatusAndDataHoraInicioBetween(
                        medico, statusSelecionado, dataHoraInicio, dataHoraFim);
            }

            if (agendas.isEmpty()) {
                model.addAttribute("mensagem", "Nenhuma agenda encontrada com os filtros informados.");
            }

            model.addAttribute("data", data);
            model.addAttribute("horarioInicio", horarioInicio);
            model.addAttribute("horarioFim", horarioFim);
            model.addAttribute("statusSelecionado", statusSelecionado);
            model.addAttribute("medicoSelecionado", medicoId);
            model.addAttribute("statusList", AgendaStatus.values());
            model.addAttribute("medicos", medicoRepository.findByStatus(StatusMedico.ATIVO));
            model.addAttribute("agendas", agendas);

            return "agenda/pesquisa";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erro ao pesquisar agendas: " + e.getMessage());
            return "redirect:/agendas";
        }
    }

    // -------------------------------
    // PESQUISA POR PERÍODO ESTENDIDO
    // -------------------------------
    @GetMapping("/pesquisar-periodo")
    public String pesquisarPorPeriodo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) AgendaStatus statusSelecionado,
            @RequestParam(name = "medicoId", required = false) Long medicoId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (dataInicio == null) {
            dataInicio = LocalDate.now();
        }
        if (dataFim == null) {
            dataFim = dataInicio.plusDays(7);
        }

        LocalDateTime dataHoraInicio = dataInicio.atStartOfDay();
        LocalDateTime dataHoraFim = dataFim.atTime(23, 59, 59);

        List<Agenda> agendas;

        try {
            boolean filtrarPorTodosMedicos = (medicoId == null);
            boolean filtrarPorTodosStatus = (statusSelecionado == null);

            if (filtrarPorTodosMedicos && filtrarPorTodosStatus) {
                agendas = agendaRepository.findByDataHoraInicioBetween(dataHoraInicio, dataHoraFim);
            } else if (filtrarPorTodosMedicos) {
                agendas = agendaRepository.findByStatusAndDataHoraInicioBetween(
                        statusSelecionado, dataHoraInicio, dataHoraFim);
            } else if (filtrarPorTodosStatus) {
                Medico medico = medicoRepository.findById(medicoId)
                        .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));
                agendas = agendaRepository.findByMedicoAndDataHoraInicioBetween(
                        medico, dataHoraInicio, dataHoraFim);
            } else {
                Medico medico = medicoRepository.findById(medicoId)
                        .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));
                agendas = agendaRepository.findByMedicoAndStatusAndDataHoraInicioBetween(
                        medico, statusSelecionado, dataHoraInicio, dataHoraFim);
            }

            model.addAttribute("dataInicio", dataInicio);
            model.addAttribute("dataFim", dataFim);
            model.addAttribute("statusSelecionado", statusSelecionado);
            model.addAttribute("medicoSelecionado", medicoId);
            model.addAttribute("statusList", AgendaStatus.values());
            model.addAttribute("medicos", medicoRepository.findByStatus(StatusMedico.ATIVO));
            model.addAttribute("agendas", agendas);

            return "agenda/pesquisa-periodo";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erro ao pesquisar agendas: " + e.getMessage());
            return "redirect:/agendas";
        }
    }

    // -------------------------------
    // GERAÇÃO AUTOMÁTICA DE AGENDAS
    // -------------------------------
    @PostMapping("/gerar-de-disponibilidade/{disponibilidadeId}")
    public String gerarAgendasDeDisponibilidade(@PathVariable Long disponibilidadeId,
                                                RedirectAttributes redirectAttributes) {
        try {
            Disponibilidade disponibilidade = disponibilidadeRepository.findById(disponibilidadeId)
                    .orElseThrow(() -> new IllegalArgumentException("Disponibilidade não encontrada"));

            List<Agenda> agendasGeradas = gerarAgendasAutomaticamente(disponibilidade);

            if (agendasGeradas.isEmpty()) {
                redirectAttributes.addFlashAttribute("warning",
                        "Nenhuma agenda foi gerada. Pode já existir agendas para este período.");
                return "redirect:/agendas/disponiveis";
            }

            agendaRepository.saveAll(agendasGeradas);

            redirectAttributes.addFlashAttribute("success",
                    String.format("Geradas %d agendas com sucesso!", agendasGeradas.size()));

            return "redirect:/agendas/disponiveis";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erro ao gerar agendas: " + e.getMessage());
            return "redirect:/disponibilidade/list";
        }
    }

    private List<Agenda> gerarAgendasAutomaticamente(Disponibilidade disponibilidade) {
        List<Agenda> agendas = new ArrayList<>();
        LocalDate dataAtual = disponibilidade.getDataInicial();

        int minutosConsulta = disponibilidade.getTempoConsulta().getHour() * 60 +
                disponibilidade.getTempoConsulta().getMinute();

        while (!dataAtual.isAfter(disponibilidade.getDataFinal())) {
            LocalTime horarioAtual = disponibilidade.getHorarioInicio();

            while (horarioAtual.isBefore(disponibilidade.getHorarioFim())) {
                if (disponibilidade.temIntervalo() &&
                        !horarioAtual.isBefore(disponibilidade.getIntervaloInicio()) &&
                        horarioAtual.isBefore(disponibilidade.getIntervaloFim())) {
                    horarioAtual = disponibilidade.getIntervaloFim();
                    continue;
                }

                LocalDateTime dataHoraInicio = dataAtual.atTime(horarioAtual);
                LocalDateTime dataHoraFim = dataHoraInicio.plusMinutes(minutosConsulta);

                if (dataHoraFim.toLocalTime().isAfter(disponibilidade.getHorarioFim())) {
                    break;
                }

                List<Agenda> conflitos = agendaRepository.findConflitosDeHorario(
                        disponibilidade.getMedico(),
                        dataHoraInicio,
                        dataHoraFim
                );

                if (conflitos.isEmpty()) {
                    Agenda agenda = new Agenda(dataHoraInicio, dataHoraFim, disponibilidade.getMedico());
                    agenda.setStatus(AgendaStatus.DISPONIVEL);
                    agendas.add(agenda);
                }

                horarioAtual = dataHoraFim.toLocalTime();
            }

            dataAtual = dataAtual.plusDays(1);
        }

        return agendas;
    }

    @GetMapping("/status/{status}")
    public String listarPorStatus(@PathVariable AgendaStatus status, Model model) {
        List<Agenda> agendas = agendaRepository.findByStatus(status);
        model.addAttribute("agendas", agendas);
        model.addAttribute("statusFiltro", status);
        model.addAttribute("statusList", AgendaStatus.values());
        return "agenda/list-por-status";
    }

    @GetMapping("/relatorio")
    public String relatorioAgendas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Model model) {

        if (dataInicio == null) {
            dataInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (dataFim == null) {
            dataFim = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }

        LocalDateTime dataHoraInicio = dataInicio.atStartOfDay();
        LocalDateTime dataHoraFim = dataFim.atTime(23, 59, 59);

        List<Agenda> todasAgendas = agendaRepository.findByDataHoraInicioBetween(dataHoraInicio, dataHoraFim);

        long totalAgendas = todasAgendas.size();
        long agendasDisponiveis = todasAgendas.stream().filter(a -> a.getStatus() == AgendaStatus.DISPONIVEL).count();
        long agendasAgendadas = todasAgendas.stream().filter(a -> a.getStatus() == AgendaStatus.AGENDADO).count();
        long agendasCanceladas = todasAgendas.stream().filter(a -> a.getStatus() == AgendaStatus.CANCELADO).count();

        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("agendas", todasAgendas);
        model.addAttribute("totalAgendas", totalAgendas);
        model.addAttribute("agendasDisponiveis", agendasDisponiveis);
        model.addAttribute("agendasAgendadas", agendasAgendadas);
        model.addAttribute("agendasCanceladas", agendasCanceladas);

        return "agenda/relatorio";
    }

    @GetMapping("/hoje")
    public String agendaDeHoje(Model model) {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioHoje = hoje.atStartOfDay();
        LocalDateTime fimHoje = hoje.atTime(23, 59, 59);

        List<Agenda> agendasHoje = agendaRepository.findByDataHoraInicioBetween(inicioHoje, fimHoje);

        model.addAttribute("agendas", agendasHoje);
        model.addAttribute("dataHoje", hoje);
        return "agenda/agenda-hoje";
    }

    @GetMapping("/liberar/{agendaId}")
    public String liberarHorario(@PathVariable Long agendaId, RedirectAttributes redirectAttributes) {
        try {
            Agenda agenda = agendaRepository.findById(agendaId)
                    .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada"));

            if (agenda.getConsulta() != null) {
                consultaRepository.delete(agenda.getConsulta());
                agenda.setConsulta(null);
            }

            agenda.setStatus(AgendaStatus.DISPONIVEL);
            agendaRepository.save(agenda);

            redirectAttributes.addFlashAttribute("success", "Horário liberado com sucesso!");
            return "redirect:/agendas/disponiveis";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erro ao liberar horário: " + e.getMessage());
            return "redirect:/agendas";
        }
    }
}
