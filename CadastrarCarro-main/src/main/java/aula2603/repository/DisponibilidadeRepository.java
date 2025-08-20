package aula2603.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class DisponibilidadeRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AgendaRepository agendaRepository;

    public void save(Disponibilidade disponibilidade) {
        if (verificarDisponibilidade(disponibilidade)) {
            em.persist(disponibilidade);
            criarSlotsDeAgenda(disponibilidade);
        }
    }

    public List<Disponibilidade> findAll() {
        Query query = em.createQuery("select d from Disponibilidade d");
        return query.getResultList();
    }

    public List<Disponibilidade> findByMes(int mes, int ano) {
        Query query = em.createQuery(
                "SELECT d FROM Disponibilidade d " +
                        "WHERE MONTH(d.dataInicial) <= :mes AND MONTH(d.dataFinal) >= :mes " +
                        "AND YEAR(d.dataInicial) = :ano AND YEAR(d.dataFinal) = :ano"
        );
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        return query.getResultList();
    }

    public Boolean verificarDisponibilidade(Disponibilidade disponibilidade) {
        Query query = em.createQuery(
                "SELECT COUNT(d) FROM Disponibilidade d " +
                        "WHERE d.medico.id = :medicoId " +
                        "AND (:dataInicial <= d.dataFinal AND :dataFinal >= d.dataInicial) " +
                        "AND ( " +
                        "    (:horarioInicio <= d.horarioFim AND :horarioFim >= d.horarioInicio) " +
                        "    OR (:horarioInicio >= d.intervaloInicio AND :horarioFim <= d.intervaloFim) " +
                        "    OR (:horarioInicio <= d.intervaloInicio AND :horarioFim >= d.intervaloFim) " +
                        "    OR (:horarioInicio >= d.intervaloInicio AND :horarioInicio <= d.intervaloFim) " +
                        "    OR (:horarioFim >= d.intervaloInicio AND :horarioFim <= d.intervaloFim) " +
                        ")"
        );

        query.setParameter("medicoId", disponibilidade.getMedico().getId());
        query.setParameter("dataInicial", disponibilidade.getDataInicial());
        query.setParameter("dataFinal", disponibilidade.getDataFinal());
        query.setParameter("horarioInicio", disponibilidade.getHorarioInicio());
        query.setParameter("horarioFim", disponibilidade.getHorarioFim());

        Long count = (Long) query.getSingleResult();
        return count == 0;
    }

    private void criarSlotsDeAgenda(Disponibilidade disponibilidade) {
        if (disponibilidade == null) return;

        LocalDate dataInicial = disponibilidade.getDataInicial();
        LocalDate dataFinal = disponibilidade.getDataFinal();
        LocalTime horarioInicio = disponibilidade.getHorarioInicio();
        LocalTime horarioFim = disponibilidade.getHorarioFim();
        LocalTime intervaloInicio = disponibilidade.getIntervaloInicio();
        LocalTime intervaloFim = disponibilidade.getIntervaloFim();
        LocalTime tempoConsulta = disponibilidade.getTempoConsulta();

        int minutosConsulta = tempoConsulta.getHour() * 60 + tempoConsulta.getMinute();
        boolean considerarIntervalo = !(intervaloInicio.equals(LocalTime.MIDNIGHT) && intervaloFim.equals(LocalTime.MIDNIGHT));

        for (LocalDate data = dataInicial; !data.isAfter(dataFinal); data = data.plusDays(1)) {
            LocalTime horaAtual = horarioInicio;

            while (!horaAtual.plusMinutes(minutosConsulta).isAfter(horarioFim)) {
                LocalTime fimConsulta = horaAtual.plusMinutes(minutosConsulta);

                boolean dentroDoIntervalo = false;
                if (considerarIntervalo) {
                    boolean inicioDuranteIntervalo = !horaAtual.isBefore(intervaloInicio) && horaAtual.isBefore(intervaloFim);
                    boolean fimDuranteIntervalo = fimConsulta.isAfter(intervaloInicio) && !fimConsulta.isAfter(intervaloFim);
                    boolean cruzaIntervalo = horaAtual.isBefore(intervaloInicio) && fimConsulta.isAfter(intervaloFim);

                    dentroDoIntervalo = inicioDuranteIntervalo || fimDuranteIntervalo || cruzaIntervalo;
                }

                if (!dentroDoIntervalo) {
                    Agenda agenda = new Agenda();
                    agenda.setData(data);
                    agenda.setHorarioInicio(horaAtual);
                    agenda.setHorarioFim(fimConsulta);
                    agenda.setMedico(disponibilidade.getMedico());
                    agenda.setPaciente(null);
                    //agenda.setFlagFormConsulta(false);
                    agenda.setStatus(StatusAgenda.DISPONIVEL);
                    agendaRepository.save(agenda);
                }
                horaAtual = fimConsulta;
            }
        }
    }
}