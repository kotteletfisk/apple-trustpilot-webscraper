package dat.utils;

import dat.entities.Registrant;
import dat.entities.Review;
import dat.config.ExecutorConfig;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TrustpilotScraper
{
    final static String baseUrl = "https://dk.trustpilot.com/review/www.apple.com";

    public Map<Integer, List<Review>> getReviews()
    {

        Map<Integer, List<Review>> output = new HashMap<>();
        try
        {
            Document doc = Jsoup.connect(baseUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36")/*.header("Accept-Language", "en-US,en;q=0.5")*/
                    .get();

            int pageCount = Integer.parseInt(doc.selectFirst("a.button_button__T34Lr:nth-child(7) > span").text());

            // Multi thread execution
            ExecutorService executorService = ExecutorConfig.getExecutorService();
            List<Future<List<Review>>> futures = new ArrayList<>();


            for (int i = 1; i <= pageCount; i++)
            {
                // Task from nested class
                PageScraper ps = new PageScraper(i);
                futures.add(executorService.submit(ps));
                // ~Dont spam the server!~ SPAM THE SERVER!
                // Thread.sleep(1000);
            }

            for (int i = 1; i <= pageCount; i++)
            {
                output.put(i, futures.get(i - 1).get());
            }

            executorService.shutdown();
        }
        catch (IOException | InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
        return output;
    }

    private static LocalDate strToLocalDate(String str)
    {
        str = str.replaceAll("\\.", "");
        String[] strArr = str.split(" ");

        if (strArr.length == 5)
        {
            strArr = new String[]{strArr[2], strArr[3], strArr[4]};
        }

        switch (strArr[1])
        {
            case "jan" -> strArr[1] = "1";
            case "feb" -> strArr[1] = "2";
            case "mar" -> strArr[1] = "3";
            case "apr" -> strArr[1] = "4";
            case "maj" -> strArr[1] = "5";
            case "jun" -> strArr[1] = "6";
            case "jul" -> strArr[1] = "7";
            case "aug" -> strArr[1] = "8";
            case "sep" -> strArr[1] = "9";
            case "okt" -> strArr[1] = "10";
            case "nov" -> strArr[1] = "11";
            case "dec" -> strArr[1] = "12";
        }

        return LocalDate.of(Integer.parseInt(strArr[2]), Integer.parseInt(strArr[1]), Integer.parseInt(strArr[0]));
    }

    private class PageScraper implements Callable<List<Review>>
    {
        private final int pageNumber;

        public PageScraper(int pageNumber)
        {
            this.pageNumber = pageNumber;
        }

        @Override
        public List<Review> call() throws Exception
        {
            return scrapePage(pageNumber);
        }

        private static List<Review> scrapePage(int pageNumber) throws IOException
        {
            String url;

            url = pageNumber == 1 ? baseUrl : baseUrl + "?page=" + pageNumber;

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36")/*.header("Accept-Language", "en-US,en;q=0.5")*/
                    .get();

            List<Review> reviews = new ArrayList<>();

            doc.select("article").forEach(article ->
            {
                Element name = article.selectFirst("span.typography_heading-xxs__QKBS8");
                Element reviewAmount = article.selectFirst("span.typography_body-m__xgxZ_");
                Element country = article.selectFirst("div.typography_body-m__xgxZ_ span");
                Element rating = article.selectFirst("div.star-rating_medium__iN6Ty img"); // get .attr("alt")
                Element title = article.selectFirst("h2");
                Element content = article.selectFirst("p");
                Element date = article.selectFirst("time");

                //TODO: Maybe add country entity?
                Registrant registrant = new Registrant(name.text(),
                        Integer.valueOf(reviewAmount.text().split(" ")[0]),
                        country.text());
                Review review = Review.builder()
                        .registrant(registrant)
                        .rating(Integer.valueOf(rating.attr("alt")
                                .split(" ")[2]))
                        .title(title.text())
                        .content(content.text())
                        .date(strToLocalDate(date.text()))
                        .build();

                registrant.addReview(review);
                reviews.add(review);
            });
            return reviews;
        }
    }
}
