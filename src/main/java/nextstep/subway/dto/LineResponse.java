package nextstep.subway.dto;

import java.util.List;
import nextstep.subway.domain.Station;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<Station> stationList;

    public LineResponse(Long id, String name, String color, List<Station> stationList) {
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

    public List<Station> getStationList() {
        return stationList;
    }

}
