package nextstep.subway.line.domain;

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

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Distance distance) {
        return new Section(upStation, downStation, distance);
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

    public List<Section> updateSection(Section inputSection) {
        Station inputUpStation = inputSection.upStation;
        Station inputDownStation = inputSection.downStation;
        Distance inputDistance = inputSection.distance;

        if (this.upStation.equals(inputUpStation)) {
            Section frontSection = Section.of(this.upStation, inputDownStation, inputDistance);
            Section backSection = Section.of(inputDownStation, this.downStation, distance.minus(inputDistance));
            return Arrays.asList(frontSection,backSection);
        }

        Section frontSection = Section.of(this.upStation, inputUpStation, distance.minus(inputDistance));
        Section backSection = Section.of(inputDownStation, this.downStation, inputDistance);
        return Arrays.asList(frontSection,backSection);
    }

}
