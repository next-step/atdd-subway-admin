package nextstep.subway.domain;

import nextstep.subway.domain.raw.Distance;
import nextstep.subway.dto.LineResponse;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static nextstep.subway.constant.Message.STATION_IS_NOT_NULL;

@Entity
public class Section implements Comparable<Section> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)        // LAZY 하면 저장 후 제다로 section정보가 Line에 반영되지 않음
    @ManyToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station_to_station"), nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station_to_station"), nullable = false)
    private Station downStation;

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @Embedded
    private Distance distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, Line line, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = new Distance(distance);
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        validateStationIsNotNull(upStation);
        validateStationIsNotNull(downStation);
        return new Section(upStation, downStation, Distance.from(distance));
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

    public int getDistance() {
        return distance.getDistance();
    }


    public boolean isContainSameStations(Section section) {
        if (Objects.isNull(section)) {
            return false;
        }
        return isUpStation(section.upStation) && isDownStation(section.downStation);
    }

    public boolean isContainAnyStaion(Section section) {
        if (Objects.isNull(section)) {
            return false;
        }
        return isUpStation(section.upStation) || isDownStation(section.upStation)
                || isUpStation(section.downStation) || isDownStation(section.downStation);
    }

    public boolean isUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public void changeUpStation(Section newSection) {
        validateStationIsNotNull(newSection.upStation);
        this.upStation = newSection.downStation;
        this.distance = subtractDistance(newSection.getDistance());
    }

    public boolean isLastStation(List<Section> sections) {
        Station firstStation = sections.get(0).upStation;
        Station lastStation = sections.get(sections.size() - 1).downStation;
        return firstStation.equals(upStation) || lastStation.equals(downStation);
    }

    public void changeDownStation(Section section) {
        validateStationIsNotNull(section.downStation);
        this.downStation = section.downStation;
        this.distance = sumDistance(section.getDistance());
    }

    private Distance subtractDistance(int distance) {
        return this.distance.subtract(distance);
    }

    public Stream<Station> stations() {
        return Stream.of(upStation, downStation);
    }

    private static void validateStationIsNotNull(Station station) {
        if (Objects.isNull(station)) {
            throw new IllegalArgumentException(STATION_IS_NOT_NULL);
        }
    }


    private Distance sumDistance(int distance) {
        return this.distance.sumDistance(distance);
    }

    public void updateLine(Line line) {
        if (line.equals(this.line)) {
            return;
        }
        this.line = line;
    }


    public LineResponse.Section from() {
        return LineResponse.Section.of(id, upStation.getId(), downStation.getId(), distance.getDistance());
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, line, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", line=" + line +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(line, section.line);
    }

    @Override
    public int compareTo(Section s) {
        if (this.downStation.getId().equals(s.getUpStation().getId())) return -1;
        else if (this.upStation.getId().equals(s.getDownStation().getId())) return 1;
        return 0;
    }


    public void addTo(Line line) {
        if (line.equals(this.line)) {
            return;
        }
        this.line = line;
    }
}
