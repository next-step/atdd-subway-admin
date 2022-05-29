package nextstep.subway.dto.response;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.StationDTO;
import nextstep.subway.enums.LineColor;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationDTO> stations = new LinkedList<>();

    protected LineResponse(Long id, String name, LineColor lineColor, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = lineColor.getColorName();
        this.stations = Arrays.asList(upStation.toStationDTO(),downStation.toStationDTO());
    }

    public static LineResponse of(Line line){
        return new LineResponse(line.getId(), line.getName(), line.getLineColor(),
            line.getUpStation(), line.getDownStation());
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

    public List<StationDTO> getStations() {
        return stations;
    }
}
