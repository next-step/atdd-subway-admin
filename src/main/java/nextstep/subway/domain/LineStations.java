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

    public void addLineStation(LineStation lineStation) {
        validateLineStation(lineStation);
        lineStations.add(lineStation);
    }

    public void addBetweenSection(LineStation lineStation) {
        Optional<LineStation> first = lineStations.stream()
                .filter(ls -> ls.getCurrentStation().isSame(lineStation.getCurrentStation()))
                .findFirst();
        if (first.isPresent()) {
            LineStation preLineStation = first.get();
            LineStation newLineStation = preLineStation.changeBetweenSection(lineStation);// 3 -> 2 : 6
            int preIndex = lineStations.indexOf(preLineStation);
            lineStations.add(preIndex + 1, newLineStation);
        }
    }

    public List<LineStation> values() {
        return Collections.unmodifiableList(lineStations);
    }

    private void validateLineStation(LineStation lineStation) {
        if (lineStations.contains(lineStation)) {
            throw new IllegalArgumentException("이미 등록되어 있는 구간입니다.");
        }
    }
}
