package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {

    public static final int END_SECTION_DISTANCE = 0;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    Integer distance;

    public static List<Section> makeInitialSections(Station upStation, Station downStation, Integer distance) {
        return Arrays.asList(
                new Section(null, upStation, END_SECTION_DISTANCE),
                new Section(upStation, downStation, distance),
                new Section(downStation, null, END_SECTION_DISTANCE));
    }

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Integer distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        this(null, upStation, downStation, distance);
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

    public Section changeUpStation(Station upStation, Integer distance) {
        Section section = new Section(line, this.upStation, upStation, calDistance(distance));
        setUpStation(upStation);
        setDistance(distance);
        return section;
    }

    public Section changeDownStation(Station downStation, Integer distance) {
        Section section = new Section(line, downStation, this.downStation, calDistance(distance));
        setDownStation(downStation);
        setDistance(distance);
        return section;
    }

    private Integer calDistance(Integer distance) {
        if (isEndSection(distance)) {
            return distance;
        }

        if (distance >= this.distance) {
            throw new IllegalArgumentException("추가되는 구간의 거리는 기존이 구간보다 크거나 같을 수 없다. ");
        }

        return this.distance - distance;
    }

    private boolean isEndSection(Integer distance) {
        return downStation == null || upStation == null;
    }

    private void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    private void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    private void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void setLine(Line line) {
        if (this.line != null) {
            this.line.getSections().remove(this);
        }
        this.line = line;
        line.addSection(this);
    }
}
