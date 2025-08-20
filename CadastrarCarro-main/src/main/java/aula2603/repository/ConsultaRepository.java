package aula2603.repository;




import aula2603.model.entity.Medico;
import aula2603.model.entity.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ConsultaRepository{

    @PersistenceContext
    private EntityManager em;

    public List<jpa.aula.model.entity.Consulta> findByPacienteIdWithMedico(Long pacienteId) {
        String jpql = "SELECT c FROM Consulta c JOIN FETCH c.paciente JOIN FETCH c.medico WHERE c.paciente.id = :pacienteId";
        return em.createQuery(jpql, jpa.aula.model.entity.Consulta.class)
                .setParameter("pacienteId", pacienteId)
                .getResultList();
    }

    public List<jpa.aula.model.entity.Consulta> findByMedicoIdWithPaciente(Long medicoId) {
        String jpql = "SELECT c FROM Consulta c JOIN FETCH c.paciente JOIN FETCH c.medico WHERE c.medico.id = :medicoId";
        return em.createQuery(jpql, jpa.aula.model.entity.Consulta.class)
                .setParameter("medicoId", medicoId)
                .getResultList();
    }

    public List<jpa.aula.model.entity.Consulta> findAllWithPacienteAndMedico() {
        String jpql = "SELECT DISTINCT c FROM Consulta c LEFT JOIN FETCH c.paciente LEFT JOIN FETCH c.medico";
        return em.createQuery(jpql, jpa.aula.model.entity.Consulta.class)
                .getResultList();
    }

    public Optional<jpa.aula.model.entity.Consulta> findByIdWithPacienteAndMedico(Long id) {
        String jpql = "SELECT c FROM Consulta c LEFT JOIN FETCH c.paciente LEFT JOIN FETCH c.medico WHERE c.id = :id";
        jpa.aula.model.entity.Consulta consulta = em.createQuery(jpql, jpa.aula.model.entity.Consulta.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);
        return Optional.ofNullable(consulta);
    }

    public List<jpa.aula.model.entity.Consulta> findByMedicoId(Long medicoId) {
        String jpql = "SELECT c FROM Consulta c WHERE c.medico.id = :medicoId";
        return em.createQuery(jpql, jpa.aula.model.entity.Consulta.class)
                .setParameter("medicoId", medicoId)
                .getResultList();
    }

    public List<jpa.aula.model.entity.Consulta> findByPacienteId(Long pacienteId) {
        String jpql = "SELECT c FROM Consulta c WHERE c.paciente.id = :pacienteId";
        return em.createQuery(jpql, jpa.aula.model.entity.Consulta.class)
                .setParameter("pacienteId", pacienteId)
                .getResultList();
    }

    public List<jpa.aula.model.entity.Consulta> findByData(LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();
        return findByDataBetween(inicio, fim);
    }

    public List<jpa.aula.model.entity.Consulta> findByDataBetween(LocalDateTime inicio, LocalDateTime fim) {
        String jpql = "SELECT c FROM Consulta c LEFT JOIN FETCH c.paciente LEFT JOIN FETCH c.medico " +
                "WHERE c.data >= :inicio AND c.data < :fim";
        return em.createQuery(jpql, jpa.aula.model.entity.Consulta.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    public long countByMedicoId(Long medicoId) {
        String jpql = "SELECT COUNT(c) FROM Consulta c WHERE c.medico.id = :medicoId";
        return em.createQuery(jpql, Long.class)
                .setParameter("medicoId", medicoId)
                .getSingleResult();
    }

    public List<jpa.aula.model.entity.Consulta> buscarPorNome(String termo) {
        String jpql = "SELECT c FROM Consulta c " +
                "LEFT JOIN FETCH c.paciente p " +
                "LEFT JOIN FETCH c.medico m " +
                "WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) " +
                "OR LOWER(m.nome) LIKE LOWER(CONCAT('%', :termo, '%'))";
        return em.createQuery(jpql, jpa.aula.model.entity.Consulta.class)
                .setParameter("termo", termo)
                .getResultList();
    }

    public List<Paciente> findPacientesByData(LocalDate data) {
        String jpql = "SELECT DISTINCT c.paciente FROM Consulta c WHERE CAST(c.data AS date) = :data";
        return em.createQuery(jpql, Paciente.class)
                .setParameter("data", data)
                .getResultList();
    }

    public boolean existsByMedicoAndDataBetween(Medico medico, LocalDateTime inicio, LocalDateTime fim) {
        String jpql = "SELECT COUNT(c) FROM Consulta c WHERE c.medico = :medico AND c.data BETWEEN :inicio AND :fim";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("medico", medico)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getSingleResult();
        return count > 0;
    }
}
