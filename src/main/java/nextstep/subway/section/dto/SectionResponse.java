package nextstep.subway.section.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.util.Objects;

public class SectionResponse {
    private final Long id;
    private final String lineName;
    private final String upStationName;
    private final String downStationName;
    private final Integer distance;

    private SectionResponse(Long id, String lineName, String upStationName, String downStationName, Integer distance) {
        this.id = id;
        this.lineName = lineName;
        this.upStationName = upStationName;
        this.downStationName = downStationName;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        Line line = section.getLine();
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        return new SectionResponse(section.getId(), line.getName(), upStation.getName(), downStation.getName(), section.getDistance());
    }

    public Long getId() {
        return id;
    }

    public String getUpStationName() {
        return upStationName;
    }

    public String getDownStationName() {
        return downStationName;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SectionResponse)) return false;
        SectionResponse that = (SectionResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(lineName, that.lineName) &&
                Objects.equals(upStationName, that.upStationName) &&
                Objects.equals(downStationName, that.downStationName) &&
                Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineName, upStationName, downStationName, distance);
    }

    @Override
    public String toString() {
        return "SectionResponse{" +
                "upStationName='" + upStationName + '\'' +
                ", downStationName='" + downStationName + '\'' +
                '}';
    }
}
