package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.Arrays;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private List<LineStationInfo> stations;

    public LineResponse(Long id, String name, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.stations = Arrays.asList(new LineStationInfo(upStation), new LineStationInfo(downStation));
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<LineStationInfo> getStations() {
        return this.stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getUpStation(), line.getDownStation());
    }

    private class LineStationInfo {
        private Long id;
        private String name;

        public LineStationInfo(Station station) {
            this.id = station.getId();
            this.name = station.getName();
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
