package aula2603.repository;

import aula2603.model.entity.Usuario;
import jakarta.persistence.*;

import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {
    @PersistenceContext
    private EntityManager em;
    public Usuario usuario(String login){
        try {
            String jpql = "from Usuario u where u.login = :login";
            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            query.setParameter("login", login);
            return query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public void save(Usuario usuario) {
        em.persist(usuario);
    }

}