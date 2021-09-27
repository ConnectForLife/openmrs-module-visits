package org.openmrs.module.visits.domain.criteria;

public class QuerySplitter {

    private String query;

    public QuerySplitter(String query) {
        this.query = query;
    }

    public String[] splitQuery() {
        return splitByWhiteCharacters(query);
    }

    private String[] splitByWhiteCharacters(String query) {
        return query.split("\\s+");
    }
}
