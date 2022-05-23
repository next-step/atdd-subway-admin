package nextstep.subway.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.Line;

public class LineResponse implements Serializable {

    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();

        nextstep.subway.domain.Station upStation = line.getUpStation();
        nextstep.subway.domain.Station downStation = line.getDownStation();
        this.stations = Arrays.asList(
            new Station(upStation.getId(), upStation.getName()),
            new Station(downStation.getId(), downStation.getName())
        );
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

    public List<Station> getStations() {
        return stations;
    }

    public static class Station {

        private Long id;
        private String name;

        public Station() {
        }

        public Station(Long id, String name) {
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

}
