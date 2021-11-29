package nextstep.subway.line.domain;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
import static nextstep.subway.line.domain.SectionType.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.DuplicationException;
import nextstep.subway.common.exception.NoResultDataException;
import nextstep.subway.line.exception.DuplicateSectionStationException;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_station"))
    private Station upStation;

    @ManyToOne(fetch = LAZY, cascade = ALL )
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_next_station"))
    private Station downStation;

    @Enumerated(EnumType.STRING)
    private SectionType sectionType;

    @Embedded
    private Distance distance = new Distance();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    private Section(Station upStation, Station downStation) {

        if (Objects.isNull(upStation)) {
            throw new NoResultDataException();
        }

        if (upStation.equals(downStation)) {
            throw new DuplicateSectionStationException();
        }
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Section(Station upStation, Station downStation, SectionType sectionType, Distance distance) {
        this(upStation, downStation);
        this.sectionType = sectionType;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, SectionType sectionType, Distance distance, Line line) {
        this(upStation, downStation, sectionType, distance);
        this.line = line;
    }

    protected Section() {
    }

    /*
     * 기능정의
     */
    public void setLine(Line line) {
        this.line = line;
    }

    public void changeUpSection(Section newSection) {
        alreadyRegisteredSection(newSection);
        if (isAddFirstSection(newSection))
            return;
        addMiddleSection(newSection);
    }

    private boolean isAddFirstSection(Section newSection) {
        if (isTypeFirstAndEqualsDownStation(newSection)) {
            changeFirstSectionType(newSection);
            return true;
        }
        return false;
    }

    private boolean isTypeFirstAndEqualsDownStation(Section newSection) {
        return SectionType.equalsFirst(sectionType) && upStation.equals(newSection.getDownStation());
    }

    public void changeDownSection(Section newSection) {
        alreadyRegisteredSection(newSection);
        this.upStation = newSection.getDownStation();
    }

    private void addMiddleSection(Section newSection) {
        downStation = newSection.getUpStation();
        distance = distance.subtract(newSection.getDistance());
    }

    void alreadyRegisteredSection(Section section) {
        if (getStationNames().containsAll(section.getStationNames())) {
            throw new DuplicationException("이미 등록되어 있는 구간입니다.");
        }
    }

    // 지하철역 이름 조회
    private List<String> getStationNames() {
        return Arrays.asList(upStation.getName(), downStation == null ? null : downStation.getName());
    }

    private void changeFirstSectionType(Section newSection) {
        this.sectionType = SectionType.MIDDLE;
        newSection.sectionType = SectionType.FIRST;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public SectionType getSectionType() {
        return sectionType;
    }

    public Long getId() {
        return id;
    }

    /*
     *  비교문
     */

    // 상행
    public boolean findUpStation(final Section newSection) {
        return upStation.equals(newSection.getDownStation());
    }

    public boolean equalsPreviousUpStation(final Section newSection) {
        final Station downStation = newSection.getDownStation();

        if (SectionType.equalsFirst(sectionType)) {
            return true;
        }

        if (SectionType.equalsLast(sectionType)) {
            return this.upStation.equals(downStation);
        }

        return this.downStation.equals(downStation);
    }

    // 하행

    public boolean equalsLastStation(final Section section) {
        return this.upStation.equals(section.getUpStation());
    }

    public boolean equalsDownStation(final Section newSection) {
        return downStation.equals(newSection.getUpStation());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return Objects.equals(id, section.id) &&
            Objects.equals(upStation, section.upStation) &&
            Objects.equals(downStation, section.downStation) &&
            sectionType == section.sectionType &&
            Objects.equals(distance, section.distance) &&
            Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, sectionType, distance, line);
    }
}
