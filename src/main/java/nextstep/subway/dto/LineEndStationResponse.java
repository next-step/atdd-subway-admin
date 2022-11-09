package nextstep.subway.dto;

public class LineEndStationResponse {
    private Long id;
    private String name;

    private LineEndStationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static LineEndStationResponse of(Long id, String name) {
        return new LineEndStationResponse(id, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
