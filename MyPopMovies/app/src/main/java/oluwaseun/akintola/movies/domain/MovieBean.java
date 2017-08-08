package oluwaseun.akintola.movies.domain;

import java.util.List;

/**
 * Created by AKINTOLA OLUWASEUN on 8/6/2017.
 */

public class MovieBean {


    private List<AllResults> results;




    public List<AllResults> getResults() {
        return results;
    }

    public void setResults(List<AllResults> results) {
        this.results = results;
    }
    public static class AllResults {

        private String original_title;
        private double vote_average;
        private String overview;
        private String release_date;
        private String poster_path;
        private String id;
        public String getId(){
            return id;
        }

        public String getOriginal_title() {

            return original_title;
        }

        public void setId(String id) {
            this.id = id;
        }


        public void setOriginal_title(String original_title) {
            this.original_title = original_title;
        }


        public double getVote_average() {
            return vote_average;
        }

        public void setVote_average(double vote_average) {
            this.vote_average = vote_average;
        }


        public String getRelease_date() {

            return release_date;
        }

        public void setRelease_date(String release_date) {
            this.release_date = release_date;
        }


        public String getPoster_path() {

            return poster_path;
        }

        public void setPoster_path(String poster_path) {
            this.poster_path = poster_path;
        }


        public String getOverview() {

            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }


        @Override
        public String toString() {
            return "RecipeBean{" +
                    "movieTitle='" + original_title + '\'' +
                    ", viewerRatings='" + vote_average + '\'' +
                    ", overview='" + overview + '\'' +
                    ", releasedDate=" + release_date +
                    ", servinposterImages='" + poster_path + '\'' +
                    '}';
        }
    }
}
