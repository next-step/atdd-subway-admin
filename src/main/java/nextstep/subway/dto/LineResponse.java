package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<Station> stations;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    public LineResponse(final Long id, final String name, final String color, final List<Station> stations, final LocalDateTime createdDate, final  LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(final Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                Arrays.asList(line.getUpStation(), line.getDownStation()),
                line.getCreatedDate(),
                line.getModifiedDate()
        );
    }

    public Long getId() {
        return id;
    }

    public boolean isContainsBy(final Station station) {
        return this.stations.contains(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineResponse that = (LineResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(color, that.color) && Objects.equals(stations, that.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, stations);
    }
}
