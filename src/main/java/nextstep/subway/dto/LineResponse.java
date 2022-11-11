package nextstep.subway.dto;


import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getUpStation(), line.getDownStation());
    }

    private LineResponse(Long id, String name, String color, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        stations = Arrays.asList(StationResponse.of(upStation), StationResponse.of(downStation));
    }

}
