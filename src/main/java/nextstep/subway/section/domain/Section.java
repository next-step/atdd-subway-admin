package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "upStation_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "downStation_id")
    private Station downStation;

    @Column
    private int distance;

    protected Section() {
    }

    private Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        Section section = new Section(line, upStation, downStation, distance);
        line.addSection(section);
        return section;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return id.equals(section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public boolean equalsUpStation(Section newSection) {
        return upStation.equals(newSection.getUpStation());
    }

    public boolean equalsDownStation(Section newSection) {
        return downStation.equals(newSection.getDownStation());
    }

    public void updateUpSection(Section newSection) {
        this.upStation = newSection.getDownStation();
        updateDistance(newSection.getDistance());
    }

    private void updateDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
        this.distance -= distance;

    }

    public boolean containsStation(Section newSection) {
        return equalsUpStation(newSection) || equalsDownStation(newSection)
                || equalsCrossStation(newSection);
    }

    private boolean equalsCrossStation(Section newSection) {
        return upStation.equals(newSection.getDownStation()) || downStation.equals(newSection.getUpStation());
    }
}
