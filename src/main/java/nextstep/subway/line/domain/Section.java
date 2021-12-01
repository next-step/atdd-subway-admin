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

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, Distance distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public static Section of(Station upStation, Station downStation, Distance distance) {
        return new Section(upStation, downStation, distance);
    }

    public static Section of(Station upStation, Station downStation, Distance distance, Line line) {
        return new Section(upStation, downStation, distance, line);
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
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

    public boolean isCreateUpSection(Section inputSection) {
        return this.upStation.equals(inputSection.downStation);
    }

    public boolean isCreateDownSection(Section inputSection) {
        return this.downStation.equals(inputSection.upStation);
    }

    public Section createInnerSection(Section newSection) {
        if (equalsUpStation(newSection)) {
            updateUpStationToNewDownStation(newSection.getDownStation(), newSection.distance);
            return newSection;
        }
        updateDownStationToNewUpStation(newSection.getUpStation(), newSection.distance);
        return newSection;
    }

    public Section createOuterSection(Section newSection) {
        if (isCreateUpSection(newSection)) {
            return Section.of(newSection.upStation, this.upStation, newSection.distance, newSection.getLine());
        }
        return Section.of(this.downStation, newSection.downStation, newSection.distance, newSection.getLine());
    }

    public Line getLine() {
        return line;
    }

    public boolean equalsUpAndDownStations(Section inputSection) {
        return equalsUpStation(inputSection) && equalsDownStation(inputSection);
    }

    public boolean isLongDistance(Section section) {
        return this.distance.isLong(section.distance);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void addLine(Line line) {
        this.line = line;
    }

    private void updateDownStationToNewUpStation(Station inputUpStation, Distance inputDistance) {
        this.downStation = inputUpStation;
        this.distance = this.distance.minus(inputDistance);
    }

    private void updateUpStationToNewDownStation(Station inputDownStation, Distance inputDistance) {
        this.upStation = inputDownStation;
        this.distance = this.distance.minus(inputDistance);
    }

    public boolean equalsUpStation(Long stationId) {
        return this.upStation.getId().equals(stationId);
    }

    public boolean equalsDownStation(Long stationId) {
        return this.downStation.getId().equals(stationId);
    }

    public void deleteLine() {
        this.line = null;
    }

    public void extendSection(Section backSection) {
        this.downStation = backSection.downStation;
        this.distance = distance.plus(backSection.distance);
    }
}
