package nextstep.subway.domain;

import nextstep.subway.domain.raw.Distance;
import nextstep.subway.dto.LineResponse;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static nextstep.subway.constant.Message.STATION_IS_NOT_NULL;

@Entity
public class Section extends BaseEntity implements Comparable<Section> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station_to_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station_to_station"))
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        validateStationIsNotNull(upStation);
        validateStationIsNotNull(downStation);
        return new Section(upStation, downStation, distance);
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

//    public void change(Section newSection) {
//        if (Objects.isNull(newSection)) {
//            return;
//        }
//        changeStation(newSection);
//    }


//    private void changeStation(Section newSection) {
//
//        // 기존 upStation과 새로운 upStation과 일치하면 사이에 들어감
//        // 따라서 기존 upStation -> 새로운 downStation
//        if (isUpStation(newSection.upStation)) {
//            changeUpStation(newSection);
//            changeDistance(newSection.distance);
//        }
//
//
//        // 기존 downStation과 새로운 downStation이 일치
//        //  따라서 기존 downStation -> 새로운 upstation
//        if (isDownStation(newSection.downStation)) {
//            changeDownStation(newSection);
//            changeDistance(newSection.distance);
//        }
//    }


    public void changeUpStation(Section newSection) {
        validateStationIsNotNull(newSection.getUpStation());
        this.upStation = newSection.downStation;
        this.distance = subtractDistance(newSection.getDistance());
    }

    public boolean isLastStation(List<Section> sections) {
        Station firstStation = sections.get(0).upStation;
        Station lastStation = sections.get(sections.size() - 1).downStation;
        return firstStation.equals(upStation) || lastStation.equals(downStation);
    }

    public void updateLine(Line line) {
        if (line.equals(this.line)) {
            return;
        }
        this.line = line;
    }

    private void updateUpStation(Section newSection) {
        this.upStation = newSection.downStation;
    }

    private void changeDownStation(Section newSection) {
        this.downStation = newSection.upStation;
    }


    private Distance subtractDistance(int distance) {
        return this.distance.subtract(distance);
    }

    public LineResponse.Section from() {
        return LineResponse.Section.builder()
                .id(id)
                .upStationId(upStation.getId())
                .downStationId(downStation.getId())
                .distance(distance.getDistance())
                .build();
    }

    private static void validateStationIsNotNull(Station station) {
        if (Objects.isNull(station)) {
            throw new IllegalArgumentException(STATION_IS_NOT_NULL);
        }
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
        //
        if (this.downStation.getId().equals(s.upStation.getId())) return -1;
        else if (this.upStation.getId().equals(s.downStation.getId())) return 1;
        return 0;
    }



}
