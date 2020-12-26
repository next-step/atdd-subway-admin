package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.exception.LineStationDuplicatedException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<LineStation> lineStations = new ArrayList<>();

    public void add(final LineStation lineStation) {
        if (contains(lineStation)) {
            String message = String.format("이미 등록된 지하철 노선 구간입니다. %s, %s-%s",
                    lineStation.getLine().getName(),
                    lineStation.getUpStation().getName(),
                    lineStation.getDownStation().getName()
            );
            throw new LineStationDuplicatedException(message);
        }
        lineStations.add(lineStation);
    }

    // todo: 임시 구현
    public List<Station> getStationsOrderByUp() {
        List<Station> orderedStations = new ArrayList<>();
        lineStations.forEach(lineStation -> addStation(orderedStations, lineStation));
        return orderedStations;
    }

    private void addStation(final List<Station> orderedStations, final LineStation lineStation) {
        orderedStations.add(lineStation.getUpStation());
        orderedStations.add(lineStation.getDownStation());
    }

    public boolean contains(final LineStation lineStation) {
        return lineStations.contains(lineStation);
    }
}
