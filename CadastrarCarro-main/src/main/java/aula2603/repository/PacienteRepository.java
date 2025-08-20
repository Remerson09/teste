package aula2603.repository;

import aula2603.model.entity.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jpa.aula.model.entity.Consulta;

import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class PacienteRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(Paciente p) {
        em.persist(p);
    }

    public Paciente paciente(Long id) {
        return em.find(Paciente.class, id);
    }

    public Paciente findById(Long id) {
        return em.find(Paciente.class, id);
    }

    public List<Paciente> pacienteList() {
        Query query = em.createQuery("from Paciente");
        return query.getResultList();
    }

    public void remove(Long id) {
        Paciente p = em.find(Paciente.class, id);
        em.remove(p);
    }

    public List<Consulta> consultaListDoPaciente(Long id) {
        Query query = em.createQuery("SELECT c FROM Consulta c WHERE c.paciente.id = :id", Consulta.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    public void update(Paciente p) {
        em.merge(p);
    }

    public List<Paciente> buscarPorPacientePelN(String nome) {
        Query query = em.createQuery("SELECT p FROM Paciente p WHERE p.nome LIKE :nome", Paciente.class);
        query.setParameter("nome", "%" + nome + "%");
        return query.getResultList();
    }

    public List<Paciente> buscarPorPacientePeloNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return Collections.emptyList();
        }

        TypedQuery<Paciente> q = em.createQuery(
                "SELECT p FROM Paciente p WHERE LOWER(p.nome) LIKE LOWER(:nome)",
                Paciente.class
        );
        q.setParameter("nome", "%" + nome.trim() + "%");
        return q.getResultList();
    }

    public Optional<Paciente> buscandoPacientePeloNomeDeUsuario(String username) {
        TypedQuery<Paciente> query = em.createQuery(
                "SELECT p FROM Paciente p WHERE p.usuario.login = :username",
                Paciente.class
        );
        query.setParameter("username", username);

        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}