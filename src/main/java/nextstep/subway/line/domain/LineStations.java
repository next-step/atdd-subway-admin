package nextstep.subway.line.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class LineStations {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private final List<LineStation> lineStations = new ArrayList<>();

    public List<LineStation> getLineStations() {
        return Collections.unmodifiableList(lineStations);
    }

    public List<LineStation> getLineStationsInOrder() {
        Optional<LineStation> preLineStation = getFirstLineStation();

        List<LineStation> result = new ArrayList<>();
        while (preLineStation.isPresent()) {
            LineStation preStationId = preLineStation.get();
            result.add(preStationId);
            preLineStation = lineStations.stream()
                    .filter(it -> it.getPreStationId() == preStationId.getStationId())
                    .findFirst();
        }
        return Collections.unmodifiableList(result);
    }

    public List<Long> getStationIds() {
        return Stream.concat(
                lineStations.stream().map(LineStation::getStationId),
                lineStations.stream().map(LineStation::getPreStationId))
                .distinct()
                .collect(Collectors.toList());
    }

    public void addLineStation(Long upStationId, Long downStationId, int distance) {
        // 새로운 역을 하행 종점으로 등록할 경우
        lineStations.stream()
                .filter(lineStation -> upStationId.equals(lineStation.getPreStationId()))
                .findFirst()
                .ifPresent(lineStation -> {
                    lineStations.add(new LineStation(lineStation.getStationId(), downStationId,
                            lineStation.getDistance() - distance));
                    lineStations.remove(lineStation);
                    lineStations.add(new LineStation(downStationId, upStationId, distance));
                });

        // 역 사이에 새로운 역을 등록할 경우
        lineStations.stream()
                .filter(lineStation -> upStationId.equals(lineStation.getStationId()))
                .findFirst()
                .ifPresent(lineStation -> {
                    lineStations.add(new LineStation(downStationId, upStationId, distance));
                });

        // 최초로 등록할 경우
        LineStation firstLineStation = getFirstLineStation().orElseGet(() -> {
            LineStation lineStation = new LineStation(upStationId, null, 0);
            lineStations.add(lineStation);
            lineStations.add(new LineStation(downStationId, upStationId, distance));
            return lineStation;
        });

        // 새로운 역을 상행 종점으로 등록할 경우
        if (firstLineStation.getStationId().equals(downStationId)) {
            firstLineStation.updatePreStationTo(upStationId);
            firstLineStation.updateDistance(distance);
            lineStations.add(new LineStation(upStationId, null, 0));
        }
    }

    private Optional<LineStation> getFirstLineStation() {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getPreStationId() == null)
                .findFirst();
    }
}
