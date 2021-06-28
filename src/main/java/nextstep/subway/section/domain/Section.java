package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidateDistanceException;
import nextstep.subway.exception.NotContainSectionException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "up_station_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @JoinColumn(name = "down_station_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @JoinColumn(name = "line_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    private long distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, long distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void validateSectionAndAddSection(long distance, Station newUpStation, Station newDownStation, List<Section> sections) {
        validateSection(distance, newUpStation, newDownStation);

        addSectionByPosition(distance, newUpStation, newDownStation, sections);
    }

    private void validateSection(long requestDistance, Station newUpStation, Station newDownStation) {
        validateAnyContainSection(newUpStation, newDownStation);
        validateDuplicateSection(newUpStation, newDownStation);
        validateSectionDistance(requestDistance, newDownStation, newUpStation);
    }

    private void validateAnyContainSection(Station newUpStation, Station newDownStation) {
        List<Station> stations = List.of(upStation, downStation);

        if (!stations.contains(newUpStation) && !stations.contains(newDownStation)) {
            throw new NotContainSectionException();
        }
    }

    private void validateDuplicateSection(Station newUpStation, Station newDownStation) {
        if (downStation.equals(newDownStation) && upStation.equals(newUpStation)) {
            throw new DuplicateSectionException();
        }
    }

    private void validateSectionDistance(long newDistance, Station newDownStation, Station newUpStation) {
        if (haveSameOneSideStation(newDownStation, newUpStation) && distance <= newDistance) {
            throw new InvalidateDistanceException();
        }
    }

    private boolean haveSameOneSideStation(Station newDownStation, Station newUpStation) {
        return downStation.equals(newDownStation) || upStation.equals(newUpStation);
    }

    private void addSectionByPosition(long newDistance, Station newUpStation, Station newDownStation, List<Section> sections) {
        if (upStation.equals(newUpStation)) {
            sections.remove(this);
            sections.add(new Section(newUpStation, newDownStation, newDistance));
            sections.add(new Section(newDownStation, downStation, distance - newDistance));
            return;
        }

        if (upStation.equals(newDownStation) || downStation.equals(newUpStation)) {
            sections.add(new Section(newUpStation, newDownStation, newDistance));
        }
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public long addDistance(Section section) {
        return this.distance + section.getDistance();
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public long getDistance() {
        return distance;
    }
}
