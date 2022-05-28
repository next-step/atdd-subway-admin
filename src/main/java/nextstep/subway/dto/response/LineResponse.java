package nextstep.subway.dto.response;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.StationDTO;
import nextstep.subway.enums.LineColor;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationDTO> stations = new LinkedList<>();

    public LineResponse(Long id, String name, LineColor lineColor, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = lineColor.getColorName();
        this.stations = Arrays.asList(upStation.toStationDTO(),downStation.toStationDTO());
    }
}
