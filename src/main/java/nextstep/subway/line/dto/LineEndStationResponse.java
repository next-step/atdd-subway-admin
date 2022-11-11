package nextstep.subway.line.dto;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineEndStationResponse that = (LineEndStationResponse) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
