package nextstep.subway.dto;

public class LineCreateResponse {
    private Long id;
    private String name;

    public LineCreateResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
