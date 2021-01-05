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

    public List<LineStation> getLineStationsInOrder() {
        Optional<LineStation> preLineStation = getTopLineStation();

        List<LineStation> lineStationsInOrder = new ArrayList<>();
        while (preLineStation.isPresent()) {
            LineStation preStationId = preLineStation.get();
            lineStationsInOrder.add(preStationId);
            preLineStation = lineStations.stream()
                    .filter(it -> it.getPreStationId() == preStationId.getStationId())
                    .findFirst();
        }
        return Collections.unmodifiableList(lineStationsInOrder);
    }

    public List<Long> getStationIds() {
        return Stream.concat(
                lineStations.stream().map(LineStation::getStationId),
                lineStations.stream().map(LineStation::getPreStationId))
                .distinct()
                .collect(Collectors.toList());
    }

    void initLineStation(Long upStationId, Long downStationId, int distance) {
        lineStations.add(new LineStation(upStationId, null, 0));
        lineStations.add(new LineStation(downStationId, upStationId, distance));
    }

    public void addLineStation(Long upStationId, Long downStationId, int distance) {
        verifyDuplicateStation(upStationId, downStationId);

        // 나눠지는 새 구간 만들기 - 새로운 역을 상행 종점으로 등록할 경우
        addNewTopLineStation(upStationId, downStationId);

        // 나눠지는 새 구간 만들기 - 역 사이에 새로운 역을 등록할 경우 - upStationId 가 이미 존재
        // 나눠지는 새 구간 만들기 - 새로운 역을 하행 종점으로 등록할 경우
        addNewMidLineStationByUpStationId(upStationId, downStationId, distance);

        // 나눠지는 새 구간 만들기 - 역 사이에 새로운 역을 등록할 경우 2 - downStationId 가 이미 존재
        addNewMidLineStationByDownStationId(upStationId, downStationId, distance);

        // 추가되는 구간은 무조건 들어감
        lineStations.add(new LineStation(downStationId, upStationId, distance));
    }

    public void removeLineStation(Long stationId) {
        verifyRemoveLineStation();

        LineStation removeLineStation = getRemoveLineStation(stationId);

        List<LineStation> lineStationsInOrder = getLineStationsInOrder();
        int index = lineStationsInOrder.indexOf(removeLineStation);

        updateNewTopLineStation(lineStationsInOrder, index);
        updateNewMidLineStation(lineStationsInOrder, index, removeLineStation);
        lineStations.remove(removeLineStation);
    }

    private LineStation getRemoveLineStation(Long stationId) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getStationId().equals(stationId))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private void verifyRemoveLineStation() {
        if (lineStations.size() <= 2)
            throw new RuntimeException();
    }

    private void updateNewMidLineStation(List<LineStation> lineStationsInOrder, int index, LineStation removeLineStation) {
        if (index < 1 || index >= lineStations.size() - 1)
            return;

        LineStation preLineStation = lineStationsInOrder.get(index - 1);
        LineStation postLineStation = lineStationsInOrder.get(index + 1);

        postLineStation.updatePreStationTo(preLineStation.getStationId());
        postLineStation.updateDistance(removeLineStation.getDistance() + postLineStation.getDistance());
    }

    private void updateNewTopLineStation(List<LineStation> lineStationsInOrder, int index) {
        if(index != 0)
            return;

        LineStation postLineStation = lineStationsInOrder.get(index + 1);
        postLineStation.updatePreStationTo(null);
        postLineStation.updateDistance(0);
    }

    private void addNewTopLineStation(Long upStationId, Long downStationId) {
        getFilteredLineStationStream(downStationId, true)
                .findFirst()
                .ifPresent(lineStation -> {
                    lineStations.add(new LineStation(upStationId, null, 0));
                    lineStations.remove(lineStation);
                });
    }

    private void addNewMidLineStationByUpStationId(Long upStationId, Long downStationId, int distance) {
        lineStations.stream()
                .filter(lineStation -> upStationId.equals(lineStation.getPreStationId()))
                .findFirst()
                .ifPresent(lineStation -> {
                    verifyOverDistance(distance, lineStation);

                    lineStations.add(new LineStation(lineStation.getStationId(), downStationId,
                            lineStation.getDistance() - distance));
                    lineStations.remove(lineStation);
                });
    }

    private void addNewMidLineStationByDownStationId(Long upStationId, Long downStationId, int distance) {
        getFilteredLineStationStream(downStationId, false)
                .findFirst()
                .ifPresent(lineStation -> {
                    verifyOverDistance(distance, lineStation);

                    lineStations.add(new LineStation(upStationId, lineStation.getPreStationId(),
                            lineStation.getDistance() - distance));
                    lineStations.remove(lineStation);
                });
    }

    private Stream<LineStation> getFilteredLineStationStream(Long downStationId, boolean isTopLineStation) {
        return lineStations.stream()
                .filter(lineStation -> downStationId.equals(lineStation.getStationId()) &&
                        (!isTopLineStation ^ lineStation.isTopLineStation()));
    }

    private void verifyDuplicateStation(Long upStationId, Long downStationId) {
        if (Stream.concat(
                getLineStationStreamByStationId(upStationId),
                getLineStationStreamByStationId(downStationId))
                .count() != 1) {
            throw new RuntimeException();
        }
    }

    private Stream<LineStation> getLineStationStreamByStationId(Long upStationId) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getStationId().equals(upStationId));
    }

    private void verifyOverDistance(int distance, LineStation lineStation) {
        if (distance >= lineStation.getDistance())
            throw new RuntimeException();
    }

    private Optional<LineStation> getTopLineStation() {
        return lineStations.stream()
                .filter(LineStation::isTopLineStation)
                .findFirst();
    }
}
