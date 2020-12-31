package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.exception.BadSectionException;
import nextstep.subway.line.exception.StationNotDeleteException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class LineStations {

    private static final int MIN_STATION = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<LineStation> lineStations = new ArrayList<>();

    public LineStations(final LineStation lineStation) {
        lineStations.add(lineStation);
    }

    public List<Station> getOrderedStations() {
        List<Station> orderedStations = new ArrayList<>();
        Station upStation = getUpStation();
        orderedStations.add(upStation);
        addOrderedStations(orderedStations, upStation);
        return orderedStations;
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

    private void addOrderedStations(final List<Station> orderedStations, final Station baseStation) {
        List<LineStation> targets = new ArrayList<>(lineStations);
        Station next = baseStation;
        while (targets.size() > 0) {
            LineStation lineStation = nextTarget(targets, next);
            Station downStation = lineStation.getDownStation();
            orderedStations.add(downStation);
            next = downStation;
        }
    }

    private LineStation nextTarget(final List<LineStation> targets, final Station next) {
        LineStation lineStation = targets.stream()
                .filter(target -> target.isUpStation(next))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
        targets.remove(lineStation);
        return lineStation;
    }

    public void add(final LineStation newLineStation) {
        boolean containsUpStation = contains(newLineStation.getUpStation());
        boolean containsDownStation = contains(newLineStation.getDownStation());

        validate(containsUpStation, containsDownStation);

        Section newSection = newLineStation.getSection();
        lineStations.stream()
                .filter(lineStation -> lineStation.canAddBetweenSection(newSection))
                .findFirst()
                .ifPresent(lineStation -> lineStation.update(newSection));
        lineStations.add(newLineStation);
    }

    private boolean contains(final Station station) {
        return lineStations.stream()
                .anyMatch(lineStation -> lineStation.contains(station));
    }

    private void validate(final boolean containsUpStation, final boolean containsDownStation) {
        if (containsUpStation && containsDownStation) {
            throw new BadSectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }

        if (!containsUpStation && !containsDownStation) {
            throw new BadSectionException("상행역과 하행역 둘 중 하나도 포함되어있지 않습니다.");
        }
    }

    public void delete(final Long stationId) {
        if (lineStations.size() <= MIN_STATION) {
            throw new StationNotDeleteException();
        }

        List<LineStation> lineStations = this.lineStations.stream()
                .filter(lineStation -> lineStation.contains(stationId))
                .collect(Collectors.toList());

        if (lineStations.size() == 0) {
            throw new StationNotFoundException(String.format("역이 존재하지 않습니다. (입력 id값: %d)", stationId));
        }

        if (lineStations.size() == 1) {
            this.lineStations.remove(lineStations.get(0));
            return;
        }

        lineStations.get(0).merge(lineStations.get(1));
        this.lineStations.remove(lineStations.get(1));
    }
}
