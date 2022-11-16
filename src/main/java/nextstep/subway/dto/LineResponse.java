package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse() {
    }

    private LineResponse(Long id, String name, String color, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        setStations(upStation, downStation);
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getUpStation(), line.getDownStation());
    }

    private void setStations(Station upStation, Station downStation){
        List<StationResponse> stations = new ArrayList<>();
        stations.add(StationResponse.of(upStation.getId(), upStation.getName()));
        stations.add(StationResponse.of(downStation.getId(), downStation.getName()));
        this.stations = stations;
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

    public List<StationResponse> getStations() {
        return stations;
    }

}
