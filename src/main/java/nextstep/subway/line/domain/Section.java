package nextstep.subway.line.domain;

import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_section_upstation"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_section_downstation"))
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation,downStation,distance);
    }

    public Section addLine(Line line) {
        this.line = line;
        return this;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean isNotContainUnAndDownStation(Section section) {
        if (!this.upStation.equals(section.upStation) && !this.upStation.equals(section.downStation)
                && !this.downStation.equals(section.upStation) && !this.downStation.equals(section.downStation)) {
            return true;
        }
        return false;
    }

    public boolean equalsUpStation(Section section) {
        return this.upStation.equals(section.upStation);
    }

    public boolean equalsDownStation(Section section) {
        return this.downStation.equals(section.downStation);
    }

    public Section updateSectionEqualUpStation(Section section) {
//        Station middleUpStation = section.upStation;
//        Station middleDownStation = section.downStation;
        Section newSection = Section.of(section.downStation, this.downStation, this.distance.minus(section.distance));
        this.downStation = section.getDownStation();
        this.distance = section.distance;
        return newSection;
    }

    public Section updateSectionEqualDownStation(Section section) {
        this.downStation = section.getUpStation();
        this.distance = new Distance(this.distance.minus(section.distance));
        return section;
    }

}
