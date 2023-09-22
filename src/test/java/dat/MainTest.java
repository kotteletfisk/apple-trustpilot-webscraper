package dat;


import dat.dao.AnalysisDAO;
import dat.dao.RegistrantDAO;
import dat.dao.ReviewDAO;
import dat.entities.Registrant;
import dat.entities.Review;
import dat.utils.TrustpilotScraper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;


class MainTest
{

    @Test
    void getReviewsTest()
    {
        TrustpilotScraper trustpilotScraper = new TrustpilotScraper();
        Map<Integer, List<Review>> map = trustpilotScraper.getReviews();

        map.forEach((key, value) -> {
            System.out.println("Page " + key);
            value.forEach(System.out::println);
        });
    }

    @Test
    void persistTest()
    {
        TrustpilotScraper scraper = new TrustpilotScraper();
        Map<Integer, List<Review>> map = scraper.getReviews();
        ReviewDAO reviewDAO = new ReviewDAO();
        RegistrantDAO registrantDAO = new RegistrantDAO();

        map.forEach((key, value) -> {
            value.forEach(reviewDAO::create);
        });

        System.out.println(reviewDAO.read(151));
    }

    @Test
    void calculatePercentageOfBadRatings()
    {
        AnalysisDAO analysisDAO = new AnalysisDAO();

        System.out.println(analysisDAO.getPercentageOfBadRatings());
    }

    @Test
    void getAverageRating()
    {
        AnalysisDAO analysisDAO = new AnalysisDAO();

        System.out.println(analysisDAO.getAverageRating());
    }

    @Test
    void getTopTenReviewers()
    {
        AnalysisDAO analysisDAO = new AnalysisDAO();

        List<Registrant> list = analysisDAO.getHighestRatingReviewers();

        for (Registrant registrant : list)
        {
            System.out.println(registrant.getName());
            registrant.getReviews().forEach(System.out::println);
        }
    }

    @Test
    void getBusiestReviewers()
    {
        AnalysisDAO analysisDAO = new AnalysisDAO();

        List<Registrant> list = analysisDAO.getBusiestReviewers();

        for (Registrant registrant : list)
        {
            System.out.println(registrant.getName() + " " + registrant.getReviewAmount());
        }
    }


}