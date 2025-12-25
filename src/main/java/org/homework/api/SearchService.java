package org.homework.api;

import javax.naming.directory.SearchResult;
import java.util.List;

public interface SearchService {
    List<SearchResult> search(String query);
    class  SearchResult {
        private final String title;
        private final String snippet;
        private final String url;
        public SearchResult(String title, String snippet, String url) {
            this.title = title;
            this.snippet = snippet;
            this.url = url;
        }
        public String getTitle() {
            return title;
        }
        public String getSnippet() {
            return snippet;
        }
        public String getUrl() {
            return url;
        }
    }
}
