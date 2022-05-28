package nextstep.subway.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import nextstep.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stationList;

    public static LineResponse of(Line line) {
        if (Objects.isNull(line)) {
            throw new IllegalArgumentException("존재하지 않은 호선입니다.");
        }

        List<StationResponse> stationList =
            Arrays.asList(StationResponse.of(line.getDownStation()), StationResponse.of(line.getUpStation()));

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationList);
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stationList) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationList = stationList;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStationList() {
        return stationList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineResponse that = (LineResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
            && Objects.equals(color, that.color) && Objects.equals(stationList, that.stationList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, stationList);
    }

}
