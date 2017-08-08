package oluwaseun.akintola.movies.domain;

import java.util.List;

/**
 * Created by AKINTOLA OLUWASEUN on 8/6/2017.
 */

public class TrailerBean {

    private List<AllResults> results;


    public List<AllResults> getResults() {
        return results;
    }

    public void setResults(List<AllResults> results) {
        this.results = results;
    }

    public static class AllResults {

        private String name;
        private String key;
        private int size;

        public void setName(String name) {
            this.name = name;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setSize(int size) {
            this.size = size;
        }


        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }

        public int getSize() {
            return size;
        }
    }
}