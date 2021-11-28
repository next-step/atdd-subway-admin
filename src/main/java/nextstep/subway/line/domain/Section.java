package nextstep.subway.line.domain;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

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
        if (SectionType.equalsFirst(sectionType)) {
            this.changeFirstSectionType(newSection);
            return;
        }
        this.downStation = newSection.getUpStation();
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
    public boolean findUpStation(Section newSection) {
        return upStation.equals(newSection.getDownStation());
    }

    public boolean equalsParentStation(Station downStation) {
        return downStation.equals(downStation);
    }

    public boolean findDownStation(Section newSection) {
        return downStation.equals(newSection.getUpStation());
    }

    public boolean equalsNextSection(Station downStation) {
        return upStation.equals(downStation);
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

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", upStation=" + upStation +
            ", downStation=" + downStation +
            ", sectionType=" + sectionType +
            ", distance=" + distance +
            '}';
    }
}
