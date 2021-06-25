package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidateDistanceException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.dto.SectionRequest;
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
        validateDuplicateSection(newUpStation, newDownStation);
        validateSectionDistance(requestDistance, newDownStation, newUpStation);
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
        addSectionWhenSameUpStation(newDistance, newUpStation, newDownStation, sections);
        addSectionWhenSameAnotherPosition(newDistance, newUpStation, newDownStation, sections);
    }

    // 일차하는 역이 둘 다 상행인 경우 해당 상행과 요청한 하행 + 상행(요청한 하행역)과 기존 하행으로 이뤄진 2개의 section으로 구성된다.
    private void addSectionWhenSameUpStation(long newDistance, Station newUpStation, Station newDownStation, List<Section> sections) {
        if (upStation.equals(newUpStation)) {
            sections.remove(this);
            sections.add(new Section(newUpStation, newDownStation, newDistance));
            sections.add(new Section(newDownStation, downStation, distance - newDistance));
        }
    }

    // 기존 하행과 요청된 상행이 일치하는 경우 기존 상행과 기존 하행 + 요청된 상행 + 요청된 하행으로 추가한다.
    private void addSectionWhenSameAnotherPosition(long newDistance, Station newUpStation, Station newDownStation, List<Section> sections) {
        if (upStation.equals(newDownStation) || downStation.equals(newUpStation)) {
            sections.add(new Section(newUpStation, newDownStation, newDistance));
        }
    }

    public void addLine(Line line) {
        this.line = line;
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
