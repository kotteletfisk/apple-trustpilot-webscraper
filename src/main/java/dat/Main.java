package dat;


import dat.entities.Review;
import dat.utils.TrustpilotScraper;

import java.util.List;
import java.util.Map;

public class Main
{
    public static void main(String[] args)
    {
        TrustpilotScraper trustpilotScraper = new TrustpilotScraper();
        Map<Integer, List<Review>> map = trustpilotScraper.getReviews();

        map.forEach((key, value) ->
        {
            System.out.println("Page " + key);
            value.forEach(System.out::println);
        });
    }
}