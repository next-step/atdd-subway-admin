package nextstep.subway.dto;

public class StationResponseForLine {
    private final Long id;
    private final String name;

    public StationResponseForLine(Long id, String name) {
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
