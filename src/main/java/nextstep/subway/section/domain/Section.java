package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "section", indexes = @Index(name = "idx_section", columnList = "line_id, up_station_id, down_station_id"))
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private Integer distance;

    protected Section() {

    }

    private Section(Line line, Station upStation, Station downStation, Integer distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section create(Line line, Station upStation, Station downStation, Integer distance) {
        return new Section(line, upStation, downStation, distance);
    }

    public Long getId() {
        return id;
    }

    public List<Section> divideByStation(Station station, Integer distance) {
        return Arrays.asList(
                Section.create(this.line, upStation, station, this.distance - distance),
                Section.create(this.line, station, downStation, distance)
        );
    }

    public Section sumBySection(Section otherSection) {
        int distance = this.distance + otherSection.getDistance();
        if(this.downStation.equals(otherSection.getUpStation())) {
            return Section.create(this.line, this.upStation, otherSection.getDownStation(), distance);
        }
        return Section.create(this.line, otherSection.upStation, this.getDownStation(), distance);
    }

    public boolean matchStation(Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }

    public boolean matchUpStationFromUpStation(Section section) {
        return this.upStation.equals(section.getUpStation());
    }

    public boolean matchUpStation(Section section) {
        return matchUpStation(section.getUpStation()) || matchUpStation(section.getDownStation());
    }

    public boolean matchUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean matchDownStationFromDownStation(Section section) {
        return this.downStation.equals(section.getDownStation());
    }

    public boolean matchDownStation(Section section) {
        return matchDownStation(section.getUpStation()) || matchDownStation(section.getDownStation());
    }

    public boolean matchDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isGreaterOrEqualDistance(Section section) {
        return this.distance >= section.getDistance();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation);
    }
}
