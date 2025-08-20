package aula2603.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class AgendaRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Agenda agenda) {
        em.persist(agenda);
    }

    public Agenda findById(Long id) {
        return em.find(Agenda.class, id);
    }

    //Buscar Todas as Agendas
    public List<Agenda> buscarTodasAgendas() {
        Query query = em.createQuery("from Agenda");
        return query.getResultList();
    }

    public List<Agenda> buscarAgendasDeTodosOsMedicos(LocalDate data, LocalTime horarioInicio, LocalTime horarioFim) {
        String jpql = """
            SELECT a FROM Agenda a
            WHERE a.data = :data
              AND a.horarioInicio >= :horarioInicio
              AND a.horarioFim <= :horarioFim
              AND a.status = :status
        """;
        TypedQuery<Agenda> query = em.createQuery(jpql, Agenda.class);
        query.setParameter("data", data);
        query.setParameter("horarioInicio", horarioInicio);
        query.setParameter("horarioFim", horarioFim);
        query.setParameter("status", StatusAgenda.DISPONIVEL);
        return query.getResultList();
    }

    // Buscar Todas agendas e todos status
    public List<Agenda> buscarTodasAgendasDeTodosMedicoseTodosStatus(LocalDate data, LocalTime horarioInicio, LocalTime horarioFim) {

        String jpql = """
        SELECT a FROM Agenda a
        WHERE a.data = :data AND a.horarioInicio >= :horarioInicio AND a.horarioFim <= :horarioFim
    """;

        TypedQuery<Agenda> query = em.createQuery(jpql, Agenda.class);
        query.setParameter("data", data);
        query.setParameter("horarioInicio", horarioInicio);
        query.setParameter("horarioFim", horarioFim);

        return query.getResultList();
    }

    // Buscar Todas agendas do dia - List
    public List<Agenda> buscarTodasAgendasDeTodosMedicoseDoStatusSelecionado(LocalDate data, LocalTime horarioInicio, LocalTime horarioFim, StatusAgenda statusAgenda) {
        StatusAgenda status = statusAgenda;

        String jpql = """
            SELECT a FROM Agenda a
            WHERE a.data = :data
              AND a.horarioInicio >= :horarioInicio
              AND a.horarioFim <= :horarioFim
              AND a.status = :status
    """;

        TypedQuery<Agenda> query = em.createQuery(jpql, Agenda.class);
        query.setParameter("status", status);
        query.setParameter("data", data);
        query.setParameter("horarioInicio", horarioInicio);
        query.setParameter("horarioFim", horarioFim);

        return query.getResultList();
    }

    // Buscar Todas agendas do dia - List
    public List<Agenda> buscarTodasAgendasDoDiaDisponiveis() {
        StatusAgenda status = StatusAgenda.DISPONIVEL;

        LocalDate dataHoje = ConfiguracaoSpringMVC.obterDataFormatada(); // deve retornar LocalDate
        LocalTime horaAtual = ConfiguracaoSpringMVC.obterHoraAtual();    // deve retornar LocalTime

        String jpql = """
        SELECT a FROM Agenda a
        WHERE a.status = :status
          AND a.data = :data
          AND a.horarioInicio >= :horaAtual
    """;

        TypedQuery<Agenda> query = em.createQuery(jpql, Agenda.class);
        query.setParameter("status", status);
        query.setParameter("data", dataHoje);
        query.setParameter("horaAtual", horaAtual);

        return query.getResultList();
    }

    public List<Agenda> buscaTodasAgendasDeUmMedicoeUmStatus(LocalDate data, LocalTime horarioInicio, LocalTime horarioFim, StatusAgenda statusAgenda, Long medicoId) {
        String jpql = """
            SELECT a FROM Agenda a
            WHERE a.medico.id = :medicoId
              AND a.data = :data
              AND a.horarioInicio >= :horarioInicio
              AND a.horarioFim <= :horarioFim
              AND a.status = :status
        """;
        TypedQuery<Agenda> query = em.createQuery(jpql, Agenda.class);
        query.setParameter("medicoId", medicoId);
        query.setParameter("data", data);
        query.setParameter("horarioInicio", horarioInicio);
        query.setParameter("horarioFim", horarioFim);
        query.setParameter("status", statusAgenda);
        return query.getResultList();
    }

    public List<Agenda> buscaTodasAgendasDeUmMedicoeTodosStatus(LocalDate data, LocalTime horarioInicio, LocalTime horarioFim, Long medicoId) {
        String jpql = """
            SELECT a FROM Agenda a
            WHERE a.medico.id = :medicoId
              AND a.data = :data
              AND a.horarioInicio >= :horarioInicio
              AND a.horarioFim <= :horarioFim
        """;
        TypedQuery<Agenda> query = em.createQuery(jpql, Agenda.class);
        query.setParameter("medicoId", medicoId);
        query.setParameter("data", data);
        query.setParameter("horarioInicio", horarioInicio);
        query.setParameter("horarioFim", horarioFim);
        return query.getResultList();
    }


    public void update(Agenda agenda) {
        em.merge(agenda);
    }
}