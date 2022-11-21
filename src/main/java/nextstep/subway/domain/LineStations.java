package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class LineStations {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public List<LineStation> getOrderdLineStations() {
        Optional<LineStation> lineStationOptional = lineStations.stream()
                .filter(it -> it.getPreStation() == null)
                .findFirst();

        List<LineStation> orderedLineStations = new ArrayList<>();

        while (lineStationOptional.isPresent()) {
            LineStation lineStation = lineStationOptional.get();
            orderedLineStations.add(lineStation);
            lineStationOptional = lineStations.stream()
                    .filter(it -> lineStation.getStation().isSame(it.getPreStation()))
                    .findFirst();
        }

        return Collections.unmodifiableList(orderedLineStations);
    }

    public void add(LineStation lineStation) {
        validateLineStation(lineStation);
        if (lineStations.isEmpty()) {
            lineStations.add(lineStation);
            return;
        }

        updateWhenAddablePre(lineStation);
        updateWhenAddablePost(lineStation);
        checkContinuable(lineStation);

        lineStations.add(lineStation);
    }

    public List<LineStation> values() {
        return Collections.unmodifiableList(lineStations);
    }

    private void updateWhenAddablePre(LineStation lineStation) {
        lineStations.stream()
                .filter(it -> lineStation.getStation().isSame(it.getStation()))
                .findFirst()
                .ifPresent(it -> it.updatePreLineStation(lineStation));
    }

    private void updateWhenAddablePost(LineStation lineStation) {
        lineStations.stream()
                .filter(it -> lineStation.getPreStation().isSame(it.getPreStation()))
                .findFirst()
                .ifPresent(it -> it.updateLineStation(lineStation));
    }

    private void checkContinuable(LineStation lineStation) {
        lineStations.stream()
                .filter(it -> lineStation.getPreStation().isSame(it.getStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private void validateLineStation(LineStation lineStation) {
        if (lineStation.getStation() == null) {
            throw new IllegalArgumentException("유효하지 않은 역입니다.");
        }
        if (lineStations.contains(lineStation)) {
            throw new IllegalArgumentException("이미 등록되어 있는 구간입니다.");
        }
    }
}
