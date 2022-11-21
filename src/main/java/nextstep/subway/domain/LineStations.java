package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class LineStations {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public void add(LineStation newLineStation) {
        validateLineStation(newLineStation);
        lineStations.add(newLineStation);
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.stream()
                .filter(lt -> lineStation.getPreStation().isSame(lt.getPreStation()))
                .findFirst()
                .ifPresent(lt -> {
                    lt.updateLineStation(lineStation);
                    lineStations.add(lineStation);
                });
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
