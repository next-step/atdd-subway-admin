package nextstep.subway.dto;

public class LineCreateRequest {
    private String name;

    public LineCreateRequest() {
    }

    public LineCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
