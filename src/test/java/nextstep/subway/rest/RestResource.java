package nextstep.subway.rest;

public enum RestResource {
    지하철_역("/stations"),
    지하철_노선("/lines"),
    지하철_구간("/sections");

    private String uri;


    RestResource(String uri) {
        this.uri = uri;
    }

    public String uri() {
        return this.uri;
    }
}
