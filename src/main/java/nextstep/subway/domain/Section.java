package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @Embedded
    private Distance distance;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pre_section_id")
    private Section preSection;

    @OneToOne(mappedBy = "preSection", cascade = CascadeType.PERSIST)
    private Section nextSection;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStationId")
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStationId")
    private Station downStation;

    protected Section() {
    }

    public Section(Line line, int distance, Station upStation, Station downStation) {
        this.line = line;
        this.distance = new Distance(distance);
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Section(Line line, int distance, Section preSection, Section nextSection,
                   Station upStation, Station downStation) {
        this.line = line;
        this.distance = new Distance(distance);
        this.preSection = preSection;
        this.nextSection = nextSection;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return line.getId();
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance.getDistance();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean isEqualsUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public boolean isEqualsDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section that = (Section) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void removeSection() {
        if (!isFirstSection()) {
            preSection.removeNextSection();
        }
        if (!isLastSection()) {
            nextSection.removePreSection();
        }
    }

    public void validateLength(int distance) {
        if (this.distance.isGreaterEqual(distance)) {
            throw new IllegalArgumentException("기존역 사이에 크거나 같은 길이의 역을 등록할 수 없습니다.");
        }
    }

    public void validateAlreadyExistsStation(Station upStation, Station downStation) {
        if (this.upStation.equals(upStation) && this.downStation.equals(downStation)) {
            throw new IllegalArgumentException("이미 존재하는 노선입니다.");
        }
    }

    public Section createBetweenSectionByUpStation(int distance, Station upStation, Station downStation) {
        Section preSection = new Section(line, distance, upStation, downStation);
        this.upStation = downStation;
        this.distance.setMinusDistance(distance);
        preSection.setNextSection(this);
        return preSection;
    }

    public Section createBetweenSectionByDownStation(int distance, Station upStation, Station downStation) {
        Section nextSection = new Section(line, distance, upStation, downStation);
        this.downStation = upStation;
        this.distance.setMinusDistance(distance);
        setNextSection(nextSection);
        return nextSection;
    }

    public Section createPrependSection(int distance, Station upStation, Station downStation) {
        Section preSection = new Section(line, distance, upStation, downStation);
        preSection.setNextSection(this);
        return preSection;
    }

    public Section createAppendSection(int distance, Station upStation, Station downStation) {
        Section nextSection = new Section(line, distance, upStation, downStation);
        setNextSection(nextSection);
        return nextSection;
    }

    public void ifExistPreSectionThenSetNextSection(Section newSection) {
        if (preSection != null) {
            preSection.setNextSection(newSection);
        }
    }

    public void ifExistNextSectionThenSetPreSection(Section newSection) {
        if (nextSection != null) {
            nextSection.setPreSection(newSection);
        }
    }

    public boolean isFirstSection() {
        return preSection == null;
    }

    public boolean isLastSection() {
        return nextSection == null;
    }

    public void ifExistNextSectionThenAddStationNames(Set<String> stationNames) {
        if (nextSection != null) {
            stationNames.add(nextSection.getUpStation().getName());
            stationNames.add(nextSection.getDownStation().getName());
            nextSection.ifExistNextSectionThenAddStationNames(stationNames);
        }
    }

    public void ifExistNextSectionThenAddDistances(List<Integer> distances) {
        if (nextSection != null) {
            distances.add(nextSection.getDistance());
            nextSection.ifExistNextSectionThenAddDistances(distances);
        }
    }

    private void removePreSection() {
        this.preSection = null;
    }

    private void removeNextSection() {
        this.nextSection = null;
    }

    private void setPreSection(Section preSection) {
        this.preSection = preSection;
    }

    private void setNextSection(Section nextSection) {
        this.nextSection = nextSection;
        nextSection.setPreSection(this);
    }
}
