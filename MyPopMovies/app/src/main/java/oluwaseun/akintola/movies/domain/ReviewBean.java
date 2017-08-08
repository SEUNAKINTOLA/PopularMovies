package oluwaseun.akintola.movies.domain;

import java.util.List;

/**
 * Created by AKINTOLA OLUWASEUN on 8/6/2017.
 */

public class ReviewBean {
    private String total_results;


    public void setTotal_results(String total_results) {
        this.total_results = total_results;
    }


    public String getTotal_results() {
        return total_results;
    }
    private List<AllResults> results;


    public List<AllResults> getResults() {
        return results;
    }

    public void setResults(List<AllResults> results) {
        this.results = results;
    }

    public static class AllResults {

        private String content;
        private String author;

        public void setContent(String content) {
            this.content = content;
        }


        public void setAuthor(String author) {
            this.author = author;
        }


        public String getContent() {
            return content;
        }

        public String getAuthor() {
            return author;
        }
    }

}
