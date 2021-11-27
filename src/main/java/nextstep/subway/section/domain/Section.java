package nextstep.subway.section.domain;

import nextstep.subway.common.Message;
import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_up_station_to_line"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_down_station_to_line"))
    private Station downStation;

    private int distance;

    protected Section() {
    }

    private Section(final Station upStation, final Station downStation, final int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(final Station upStation, final Station downStation, final int distance){
        return new Section(upStation, downStation, distance);
    }

    public Section(final Long id, final Line line, final Station upStation, final Station downStation, final int distance){
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public void changeUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void changeDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void minusDistance(int distance){
        if (this.getDistance() <= distance){
            throw new IllegalArgumentException(Message.SECTION_DISTANCE_REGISTER.getMessage());
        }
        this.distance = this.distance - distance;
    }

    public boolean isUpStationEquals(Section section){
        return this.getUpStation().equals(section.getUpStation());
    }

    public boolean isDownStationEquals(Section section){
        return this.getDownStation().equals(section.getDownStation());
    }

    public boolean isUpStationAndTargetDownStationEquals(Section section){
        return this.getUpStation().equals(section.getDownStation());
    }

    public boolean isDownStationAndTargetUpStationEquals(Section section){
        return this.getDownStation().equals(section.getUpStation());
    }

    public void setLine(Line line) {
        this.line = line;
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