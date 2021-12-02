package nextstep.subway.section.domain;

import nextstep.subway.common.Message;
import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.common.exception.RegisterDistanceException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_up_station_to_line"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_down_station_to_line"))
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(final Long id, final Line line, final Station upStation, final Station downStation, final int distance){
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private Section(final Station upStation, final Station downStation, final int distance) {
        this(null, null, upStation, downStation, distance);
    }

    public static Section of(final Station upStation, final Station downStation, final int distance){
        return new Section(upStation, downStation, distance);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void changeUpStation(Section section) {
        this.upStation = section.upStation;
    }

    public void changeDownStation(Section section) {
        this.downStation = section.downStation;
    }

    public void minusDistance(Section section){
        if (this.distance <= section.distance){
            throw new RegisterDistanceException(Message.NOT_REGISTER_SECTION_DISTANCE.getMessage());
        }
        this.distance -= section.distance;
    }

    public boolean isUpStationEquals(Section section){
        return this.upStation.equals(section.upStation);
    }

    public boolean isUpStationEquals(Station station){
        return this.upStation.equals(station);
    }

    public boolean isDownStationEquals(Section section){
        return this.downStation.equals(section.downStation);
    }

    public boolean isDownStationEquals(Station station){
        return this.downStation.equals(station);
    }

    public boolean isUpStationAndTargetDownStationEquals(Section section){
        return this.upStation.equals(section.downStation);
    }

    public boolean isDownStationAndTargetUpStationEquals(Section section){
        return this.downStation.equals(section.upStation);
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public List<Station> getStations() {
        return Arrays.asList(downStation, upStation);
    }

    public void merge(Section targetSection) {
        this.downStation = targetSection.downStation;
        this.distance = this.distance + targetSection.distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance
                && Objects.equals(id, section.id)
                && Objects.equals(line, section.line)
                && Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}