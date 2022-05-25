package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineResponseDTO {

    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;


    public static LineResponseDTO of(Line line){
        List<StationResponse> stations = new ArrayList<>();
        for (Station station : line.getStations().getStations()){
            stations.add(StationResponse.of(station));
        }
        return new LineResponseDTO(line.getId(),line.getName(),line.getColor(),stations);
    }

    private LineResponseDTO(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
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
