package aula2603.repository;

import aula2603.model.entity.Medico;
import aula2603.model.entity.StatusMedico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class MedicoRepository {

    @PersistenceContext
    private EntityManager em;

    public Medico findByCrm(String crm) {
        String jpql = "SELECT m FROM Medico m WHERE m.crm = :crm";
        return em.createQuery(jpql, Medico.class)
                .setParameter("crm", crm)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<Medico> findByNomeContainingIgnoreCase(String nome) {
        String jpql = "SELECT m FROM Medico m WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%'))";
        return em.createQuery(jpql, Medico.class)
                .setParameter("nome", nome)
                .getResultList();
    }

    public List<Medico> findByCrmContainingIgnoreCase(String crm) {
        String jpql = "SELECT m FROM Medico m WHERE LOWER(m.crm) LIKE LOWER(CONCAT('%', :crm, '%'))";
        return em.createQuery(jpql, Medico.class)
                .setParameter("crm", crm)
                .getResultList();
    }

    public List<Medico> findByNomeOrCrmContaining(String termo) {
        String jpql = "SELECT m FROM Medico m " +
                "WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :termo, '%')) " +
                "OR LOWER(m.crm) LIKE LOWER(CONCAT('%', :termo, '%'))";
        return em.createQuery(jpql, Medico.class)
                .setParameter("termo", termo)
                .getResultList();
    }

    public boolean existsByCrm(String crm) {
        String jpql = "SELECT COUNT(m) FROM Medico m WHERE m.crm = :crm";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("crm", crm)
                .getSingleResult();
        return count > 0;
    }

    public long countByCrm(String crm) {
        String jpql = "SELECT COUNT(m) FROM Medico m WHERE m.crm = :crm";
        return em.createQuery(jpql, Long.class)
                .setParameter("crm", crm)
                .getSingleResult();
    }

    public List<Medico> findAllWithConsultas() {
        String jpql = "SELECT DISTINCT m FROM Medico m LEFT JOIN FETCH m.consultaList";
        return em.createQuery(jpql, Medico.class)
                .getResultList();
    }

    public Optional<Medico> findByIdWithConsultas(Long id) {
        String jpql = "SELECT m FROM Medico m LEFT JOIN FETCH m.consultaList WHERE m.id = :id";
        Medico medico = em.createQuery(jpql, Medico.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);
        return Optional.ofNullable(medico);
    }

    public List<Medico> findAllWithAgendas() {
        String jpql = "SELECT DISTINCT m FROM Medico m LEFT JOIN FETCH m.agendaList";
        return em.createQuery(jpql, Medico.class)
                .getResultList();
    }

    public List<Medico> findAllByOrderByNomeAsc() {
        String jpql = "SELECT m FROM Medico m ORDER BY m.nome ASC";
        return em.createQuery(jpql, Medico.class)
                .getResultList();
    }

    public List<Medico> findByCrmNumero(String numero) {
        String jpql = "SELECT m FROM Medico m WHERE m.crm LIKE CONCAT('CRM-%', :numero, '%')";
        return em.createQuery(jpql, Medico.class)
                .setParameter("numero", numero)
                .getResultList();
    }

    public List<Medico> findByStatus(StatusMedico statusMedico) {
        String jpql = "SELECT m FROM Medico m WHERE m.status = :status";
        return em.createQuery(jpql, Medico.class)
                .setParameter("status", statusMedico)
                .getResultList();
    }
}
