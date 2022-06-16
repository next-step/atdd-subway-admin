package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.StationResponse;

@Embeddable
public class LineStations {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public void addFinalStations(final Line line,
                                 final Station finalUpStation,
                                 final Station finalDownStation,
                                 final long distance) {
        lineStations.add(new LineStation(line, finalUpStation, finalDownStation, distance));
        lineStations.add(new LineStation(line, finalDownStation, null, 0L));
    }

    public void addStationBySection(final Line line,
                                    final Station upStation,
                                    final Station downStation,
                                    final long distance) {
        validateNewDistance(distance);
        final boolean upStationExists = hasRelationTo(upStation);
        final boolean downStationExists = hasRelationTo(downStation);
        validateStations(upStationExists, downStationExists);
        if (upStationExists) {
            addDownStation(line, upStation, downStation, distance);
        }
        if (downStationExists) {
            addUpStation(line, upStation, downStation, distance);
        }
    }

    private void validateNewDistance(final long newDistance) {
        if (newDistance <= 0L) {
            throw new IllegalArgumentException("등록할 구간의 거리는 0 이하일 수 없습니다");
        }
    }

    private boolean hasRelationTo(final Station station) {
        return lineStations
                .stream()
                .anyMatch(lineStation -> lineStation.getStation().equals(station));
    }

    private void validateStations(final boolean upStationExists, final boolean downStationExists) {
        if (upStationExists && downStationExists) {
            throw new IllegalArgumentException("이미 등록된 구간입니다");
        }
        if (!upStationExists && !downStationExists) {
            throw new IllegalArgumentException("적어도 1개 지하철역은 기존에 등록된 역이어야 합니다");
        }
    }

    private void addDownStation(final Line line,
                                final Station upStation,
                                final Station downStation,
                                final long distance) {
        final LineStation upStationRelation = relationOf(upStation);
        if (upStationRelation.hasNext()) {
            validateNewDistanceByOldDistance(distance, upStationRelation.getDistanceToNext());
        }
        lineStations.add(new LineStation(
                line,
                downStation,
                upStationRelation.getNext(),
                Math.abs(upStationRelation.getDistanceToNext() - distance)));
        upStationRelation.updateNext(downStation, distance);
    }

    private LineStation relationOf(final Station station) {
        return lineStations
                .stream()
                .filter(lineStation -> station.equals(lineStation.getStation()))
                .findFirst()
                .orElse(null);
    }

    private void addUpStation(final Line line,
                              final Station upStation,
                              final Station downStation,
                              final long distance) {
        final LineStation previous = previousOf(downStation);
        if (null != previous) {
            validateNewDistanceByOldDistance(distance, previous.getDistanceToNext());
            previous.updateNext(upStation, previous.getDistanceToNext() - distance);
        }
        lineStations.add(new LineStation(line, upStation, downStation, distance));
    }

    private LineStation previousOf(final Station station) {
        return lineStations
                .stream()
                .filter(lineStation -> station.equals(lineStation.getNext()))
                .findFirst()
                .orElse(null);
    }

    private void validateNewDistanceByOldDistance(final long newDistance, final long oldDistance) {
        if (newDistance >= oldDistance) {
            throw new IllegalArgumentException("신규 구간의 길이는 기존 구간의 길이보다 짧아야 합니다.");
        }
    }

    public void removeStation(final Station station) {
        checkNumberOfStationsBeforeDelete();
        final LineStation target = getDeleteTargetOrElseThrow(station);
        updatePreviousBeforeDelete(target);
        lineStations.remove(target);
    }

    private void checkNumberOfStationsBeforeDelete() {
        if (lineStations.size() < 3) {
            throw new IllegalStateException("구간이 하나일 경우 역을 삭제할 수 없습니다.");
        }
    }

    private LineStation getDeleteTargetOrElseThrow(final Station station) {
        final LineStation target = relationOf(station);
        if (null == target) {
            throw new IllegalArgumentException("등록되지 않은 역은 삭제할 수 없습니다.");
        }
        return target;
    }

    private void updatePreviousBeforeDelete(final LineStation deleteTarget) {
        final LineStation previous = previousOf(deleteTarget.getStation());
        if (null != previous) {
            previous.updateNext(
                    deleteTarget.getNext(),
                    previous.getDistanceToNext() + deleteTarget.getDistanceToNext());
        }
    }

    public List<StationResponse> stations() {
        return lineStations
                .stream()
                .map(LineStation::getStation)
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    List<SectionResponse> sections() {
        final List<SectionResponse> sections = new ArrayList<>();
        if (!lineStations.isEmpty()) {
            lineStations.forEach(lineStation -> addSection(sections, lineStation));
        }
        return sections;
    }

    private void addSection(final List<SectionResponse> sections, final LineStation lineStation) {
        if (null != lineStation.getNext()) {
            sections.add(new SectionResponse(
                    lineStation.getLine().getName(),
                    lineStation.getStation().getName(),
                    lineStation.getNext().getName(),
                    lineStation.getDistanceToNext()));
        }
    }
}
