package dat.dao;

import dat.config.ExecutorConfig;
import dat.config.HibernateConfig;
import dat.entities.Review;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ReviewDAO
{
    EntityManagerFactory emf;
    RegistrantDAO registrantDAO;
    ExecutorService executorService;

    public ReviewDAO()
    {
        this.emf = HibernateConfig.getEntityManagerFactoryConfig("trustpilot", "create");
        this.registrantDAO = new RegistrantDAO();
        this.executorService = ExecutorConfig.getExecutorService();
    }

    public void create(Review review)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            if (registrantDAO.read(review.getRegistrant().getName()) == null)
            {
                registrantDAO.create(review.getRegistrant());
            }
            em.persist(review);
            em.getTransaction().commit();
        }
    }

    public Review read(int i)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.find(Review.class, i);
        }
    }

    public List<Review> getAllReviews()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            String jpql = "SELECT r FROM Review r";
            return em.createQuery(jpql, Review.class).getResultList();
        }
    }
}
