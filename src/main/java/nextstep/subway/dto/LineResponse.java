package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LineResponse {
    private long id;
    private String name;
    private String color;
    private List<StationOfLineResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Line line) {
        id = line.getId();
        name = line.getName();
        color = line.getColor();
        stations =
                line.getLineStations()
                        .stream()
                        .map(lineStation -> new StationOfLineResponse(
                                lineStation.getStation().getId(),
                                lineStation.getStation().getName()
                        ))
                        .collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationOfLineResponse> getStations() {
        return stations;
    }

    public static class StationOfLineResponse {
        private Long id;
        private String name;

        public StationOfLineResponse(Long id, String name) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineResponse that = (LineResponse) o;
        boolean stationsSame = stations.size() != that.stations.size();
        if (!stationsSame) {
            return false;
        }
        for (int i = 0; i < stations.size(); ++i) {
            if (
                    stations.get(i).getId() != that.stations.get(i).getId() ||
                            !stations.get(i).getName().equals(that.stations.get(i).getName())
            ) {
                stationsSame = false;
                break;
            }
        }
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(color, that.color) &&
                stationsSame;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, stations);
    }
}
