package dat.dao;

import dat.config.ExecutorConfig;
import dat.config.HibernateConfig;
import dat.entities.Registrant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AnalysisDAO
{
    EntityManagerFactory emf;
    ExecutorService executorService;

    public AnalysisDAO()
    {
        this.emf = HibernateConfig.getEntityManagerFactoryConfig("trustpilot", "update");
        this.executorService = ExecutorConfig.getExecutorService();
    }

    public float getPercentageOfBadRatings()
    {
        float result = -1;
        try (EntityManager em = emf.createEntityManager())
        {
            /*float totalReviews = em.createQuery(jpql, Long.class).getSingleResult();
            jpql = "SELECT COUNT(r) FROM Review r WHERE r.rating < 3";
            float badReviews = em.createQuery(jpql, Long.class).getSingleResult();*/

            // WHY NOT MULTITHREAD
            FindTask<Float> totalReviewsTask = new FindTask<>(em, "SELECT COUNT(r) FROM Review r", Float.class);
            FindTask<Float> badReviewsTask = new FindTask<>(em, "SELECT COUNT(r) FROM Review r WHERE r.rating < 3", Float.class);

            Future<Float> totalFuture = executorService.submit(totalReviewsTask);
            Future<Float> badRevsFuture = executorService.submit(badReviewsTask);
            executorService.shutdown();

            result = badRevsFuture.get() / totalFuture.get() * 100;
        }
        catch (ExecutionException | InterruptedException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public float getAverageRating()
    {

        try (EntityManager em = emf.createEntityManager())
        {
            String jpql = "SELECT AVG(r.rating) FROM Review r";
            return em.createQuery(jpql, Float.class).getSingleResult();
        }
    }

    public List<Registrant> getHighestRatingReviewers()
    {
        String jpql = "SELECT r.registrant\n" +
                "FROM Review r\n" +
                "GROUP BY r.registrant\n" +
                "ORDER BY AVG(r.rating) DESC\n" +
                "LIMIT 10\n";

        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery(jpql, Registrant.class).getResultList();
        }
    }

    public List<Registrant> getBusiestReviewers()
    {
        String jpql = "SELECT r FROM Registrant r\n" +
                "ORDER BY r.reviewAmount DESC\n" +
                "LIMIT 10\n";
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery(jpql, Registrant.class).getResultList();
        }
    }

    private class FindTask<T> implements Callable<T>
    {
        private final EntityManager em;
        private final String jpql;

        private final Class<T> resultType;
        public FindTask(EntityManager em, String jpql, Class<T> resultType)
        {
            this.em = em;
            this.jpql = jpql;
            this.resultType = resultType;
        }

        @Override
        public T call() throws Exception
        {
            return em.find(resultType, jpql);
        }
    }
}
