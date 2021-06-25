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

    public void validateSectionAndAddSection(SectionRequest request, Station newUpStation, Station newDownStation, List<Section> sections) {
        validateSection(request, newUpStation, newDownStation);

        addSectionByPosition(request, newUpStation, newDownStation, sections);
    }

    private void addSectionByPosition(SectionRequest request, Station newUpStation, Station newDownStation, List<Section> sections) {
        addSectionWhenSameUpStation(request, newUpStation, newDownStation, sections);
        addSectionWhenSameAnotherPosition(request, newUpStation, newDownStation, sections);
    }

    private void validateSection(SectionRequest request, Station newUpStation, Station newDownStation) {
        validateDuplicateSection(newUpStation, newDownStation);
        validateSectionDistance(request, newDownStation, newUpStation);
    }

    private void validateDuplicateSection(Station newUpStation, Station newDownStation) {
        if (downStation.equals(newDownStation) && upStation.equals(newUpStation)) {
            throw new DuplicateSectionException();
        }
    }

    private void validateSectionDistance(SectionRequest request, Station newDownStation, Station newUpStation) {
        if (downStation.equals(newDownStation) || upStation.equals(newUpStation)) {
            if (distance <= request.getDistance()) {
                throw new InvalidateDistanceException();
            }
        }
    }

    // 일차하는 역이 둘 다 상행인 경우 해당 상행과 요청한 하행 + 상행(요청한 하행역)과 기존 하행으로 이뤄진 2개의 section으로 구성된다.
    private void addSectionWhenSameUpStation(SectionRequest request, Station newUpStation, Station newDownStation, List<Section> sections) {
        if (upStation.equals(newUpStation)) {
            sections.remove(this);
            sections.add(new Section(newUpStation, newDownStation, request.getDistance()));
            sections.add(new Section(newDownStation, downStation, distance - request.getDistance()));
        }
    }

    // 요청한 하행과 기존 상행이 일치하는 경우에는 요청한 상행 + 요청한 하행과 기존 상행 + 기존 하행으로 총 2개 section이 생긴다.
    // 기존 하행과 요청된 상행이 일치하는 경우 기존 상행과 기존 하행 + 요청된 상행 + 요청된 하행으로 추가한다.
    private void addSectionWhenSameAnotherPosition(SectionRequest request, Station newUpStation, Station newDownStation, List<Section> sections) {
        if (upStation.equals(newDownStation) || downStation.equals(newUpStation)) {
            sections.add(new Section(newUpStation, newDownStation, request.getDistance()));
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
