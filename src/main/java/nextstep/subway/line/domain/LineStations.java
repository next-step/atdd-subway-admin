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
        Optional<LineStation> preLineStation = getTopLineStation();

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
        // top 종점을 일단 만들기 - 최초로 등록할 경우
        LineStation topLineStation = getTopLineStation().orElseGet(() -> {
            LineStation newTopLineStation = new LineStation(upStationId, null, 0);
            lineStations.add(newTopLineStation);
            return newTopLineStation;
        });

        // 나눠지는 새 구간 만들기 - 새로운 역을 상행 종점으로 등록할 경우
        if (topLineStation.getStationId().equals(downStationId)) {
            lineStations.add(new LineStation(upStationId, null, 0));
            lineStations.remove(topLineStation);
        }

        // 나눠지는 새 구간 만들기 - 역 사이에 새로운 역을 등록할 경우 and 새로운 역을 하행 종점으로 등록할 경우
        lineStations.stream()
                .filter(lineStation -> upStationId.equals(lineStation.getPreStationId()))
                .findFirst()
                .ifPresent(lineStation -> {
                    verifyOverDistance(distance, lineStation);

                    lineStations.add(new LineStation(lineStation.getStationId(), downStationId,
                            lineStation.getDistance() - distance));
                    lineStations.remove(lineStation);
                });

        // 나눠지는 새 구간 만들기 - 역 사이에 새로운 역을 등록할 경우 2
        lineStations.stream()
                .filter(lineStation -> !topLineStation.isSame(lineStation))
                .filter(lineStation -> downStationId.equals(lineStation.getStationId()))
                .findFirst()
                .ifPresent(lineStation -> {
                    verifyOverDistance(distance, lineStation);

                    lineStations.add(new LineStation(upStationId, lineStation.getPreStationId(),
                            lineStation.getDistance() - distance));
                    lineStations.remove(lineStation);
                });

        // 추가되는 구간은 무조건 들어감
        lineStations.add(new LineStation(downStationId, upStationId, distance));
    }

    private void verifyOverDistance(int distance, LineStation lineStation) {
        if (distance >= lineStation.getDistance())
            throw new RuntimeException();
    }

    private Optional<LineStation> getTopLineStation() {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getPreStationId() == null)
                .findFirst();
    }
}
