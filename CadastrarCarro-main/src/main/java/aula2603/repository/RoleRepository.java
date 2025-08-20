package aula2603.repository;

import aula2603.model.entity.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.List;

@Repository
public class RoleRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(Paciente p) {
        em.persist(p);
    }

    public Role findByNome(String nome) {
        List<Role> roles = em.createQuery("SELECT r FROM Role r WHERE r.nome = :nome", Role.class)
                .setParameter("nome", nome)
                .getResultList();
        return roles.isEmpty() ? null : roles.get(0);
    }

    public List<Role> findAll() {
        return em.createQuery("SELECT r FROM Role r", Role.class).getResultList();
    }

    public List<Role> findAllById(List<Long> ids) {
        return em.createQuery("SELECT r FROM Role r WHERE r.id IN :ids", Role.class)
                .setParameter("ids", ids)
                .getResultList();
    }

}
