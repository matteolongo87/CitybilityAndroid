package net.citybility.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CALL.
 */
public class Call {

    private Long id;
    /** Not-null value. */
    private String url;
    /** Not-null value. */
    private String query;
    private boolean usePost;

    public Call() {
    }

    public Call(Long id) {
        this.id = id;
    }

    public Call(Long id, String url, String query, boolean usePost) {
        this.id = id;
        this.url = url;
        this.query = query;
        this.usePost = usePost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getUrl() {
        return url;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUrl(String url) {
        this.url = url;
    }

    /** Not-null value. */
    public String getQuery() {
        return query;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setQuery(String query) {
        this.query = query;
    }

    public boolean getUsePost() {
        return usePost;
    }

    public void setUsePost(boolean usePost) {
        this.usePost = usePost;
    }

}