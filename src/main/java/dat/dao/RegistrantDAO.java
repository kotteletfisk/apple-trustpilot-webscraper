package dat.dao;

import dat.config.HibernateConfig;
import dat.entities.Registrant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class RegistrantDAO
{
    EntityManagerFactory emf;

    public RegistrantDAO()
    {
        this.emf = HibernateConfig.getEntityManagerFactoryConfig("trustpilot", "create");
    }

    public void create(Registrant registrant)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.persist(registrant);
            em.getTransaction().commit();
        }
    }

    public Registrant read(String name)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.find(Registrant.class, name);
        }
    }
}
