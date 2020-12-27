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
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
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

    public List<Station> getStationsOrderByUp() {
        List<Station> orderedStations = new ArrayList<>();
        // 상행역 찾기
        Station upStation = getUpStation();
        orderedStations.add(upStation);

        // 정렬
        addStationsOrderByUp(orderedStations, upStation);
        return orderedStations;
    }

    private void addStationsOrderByUp(final List<Station> orderedStations, final Station baseStation) {
        List<LineStation> targets = new ArrayList<>(lineStations);
        Station next = baseStation;
        while (targets.size() > 0) {
            LineStation lineStation = nextTarget(targets, next);
            Station downStation = lineStation.getDownStation();
            orderedStations.add(downStation);
            next = downStation;
        }
    }

    private Station getUpStation() {
        return lineStations.stream()
                .map(LineStation::getUpStation)
                .filter(this::isUpStation)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private boolean isUpStation(final Station station) {
        return lineStations.stream()
                .noneMatch(lineStation -> lineStation.isDownStation(station));
    }

    private LineStation nextTarget(final List<LineStation> targets, final Station next) {
        LineStation lineStation = targets.stream()
                .filter(target -> target.isUpStation(next))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
        targets.remove(lineStation);
        return lineStation;
    }

    public boolean contains(final LineStation lineStation) {
        return lineStations.contains(lineStation);
    }
}
