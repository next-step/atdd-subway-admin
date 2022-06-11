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

    public void addUpStation(final Line line, final Station upStation, final Station downStation, final long distance) {
        final LineStation previous = previousOf(downStation);
        if (null != previous) {
            validateDistance(distance, previous.getDistanceToNext());
            previous.updateNext(upStation, previous.getDistanceToNext() - distance);
        }
        lineStations.add(new LineStation(line, upStation, downStation, distance));
    }

    public void addDownStation(final Line line, final Station upStation, final Station downStation,
                               final long distance) {
        final LineStation upStationRelation = relationOf(upStation);
        if (upStationRelation.hasNext()) {
            validateDistance(distance, upStationRelation.getDistanceToNext());
        }
        lineStations.add(new LineStation(
                line,
                downStation,
                upStationRelation.getNext(),
                Math.abs(upStationRelation.getDistanceToNext() - distance)));
        upStationRelation.updateNext(downStation, distance);
    }

    public List<StationResponse> stations() {
        return lineStations
                .stream()
                .map(LineStation::getStation)
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    boolean hasRelationTo(final Station station) {
        return lineStations
                .stream()
                .anyMatch(lineStation -> lineStation.getStation().equals(station));
    }

    List<SectionResponse> sections() {
        final List<SectionResponse> sections = new ArrayList<>();
        if (!lineStations.isEmpty()) {
            lineStations.forEach(lineStation -> addSection(sections, lineStation));
        }
        return sections;
    }

    private LineStation previousOf(final Station station) {
        return lineStations
                .stream()
                .filter(lineStation -> station.equals(lineStation.getNext()))
                .findFirst()
                .orElse(null);
    }

    private LineStation relationOf(final Station station) {
        return lineStations
                .stream()
                .filter(lineStation -> station.equals(lineStation.getStation()))
                .findFirst()
                .orElse(null);
    }

    private void validateDistance(final long newDistance, final long oldDistance) {
        if (newDistance >= oldDistance) {
            throw new IllegalArgumentException("신규 구간의 길이는 기존 구간의 길이보다 짧아야 합니다.");
        }
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
