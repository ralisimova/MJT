package bg.sofia.uni.fmi.mjt.newsFeed;

import java.util.Set;

public class Criteria {

    // required parameters
    private Set<String> keywords;

    // optional parameters
    private String category;
    private String country;

    public Set<String> getKeywords() {
        return keywords;
    }

    public String getCategory() {
        return category;
    }

    public String getCountry() {
        return country;
    }


    public static CriteriaBuilder builder(Set<String> keywords) {
        return new CriteriaBuilder(keywords);
    }

    private Criteria(CriteriaBuilder builder) {
        this.keywords = builder.keywords;
        this.category = builder.category;
        this.country = builder.country;
    }

    // Builder Class
    public static class CriteriaBuilder {

        // required parameters
        private Set<String> keywords;

        // optional parameters
        private String category;
        private String country;

        public CriteriaBuilder(Set<String> keywords) {
            this.keywords = keywords;
        }

      /*  public CriteriaBuilder setCategory(String category) {
            this.category = category;
            return this;
        }*/

        public CriteriaBuilder setCountry(String country) {
            this.country = country;
            return this;
        }


        public Criteria build() {
            return new Criteria(this);
        }

    }

    public String criteriaToRequest() {
        StringBuilder result = new StringBuilder(new String());
        if (country != null) {
            result = new StringBuilder("country=" + getCountry() + "&");
        }
        if (category != null) {
            result.append("category=").append(getCategory()).append("&");
        }
        result.append("q=");
        for (String q : keywords) {
            result.append(q).append("+");
        }
        result.deleteCharAt(result.length()-1);
        return result.toString();
    }
}