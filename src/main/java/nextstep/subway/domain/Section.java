package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    private static final String INVALID_DISTANCE_EXCEPTION = "유효하기 않은 거리로는 구간을 생성할 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @ManyToOne
    @JoinColumn(name = "upStation_id", foreignKey = @ForeignKey(name = "fk_section_to_station_1"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "downStation_id", foreignKey = @ForeignKey(name = "fk_section_to_station_2"))
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    protected Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean hasUpStation(Station station) {
        return this.upStation == station;
    }

    public boolean hasDownStation(Station station) {
        return this.downStation == station;
    }

    public boolean containsStation(Station station) {
        return this.upStation == station || this.downStation == station;
    }

    public Long getLineId() {
        return line.getId();
    }

    public void switchValue(Section newSection) {

        if (upStation == newSection.upStation) {
            upStation = newSection.downStation;
            setValidateDistance(newSection.distance);
        }

        if (downStation == newSection.downStation) {
            downStation = newSection.upStation;
            setValidateDistance(newSection.distance);
        }
    }

    public void setValidateDistance(int newDistance) {
        distance -= newDistance;

        if (distance <= 0) {
            System.out.println(INVALID_DISTANCE_EXCEPTION);
            throw new IllegalArgumentException(INVALID_DISTANCE_EXCEPTION);
        }
    }

    public void combineSection(Section deleteSection) {
        downStation = deleteSection.downStation;
        distance += deleteSection.distance;
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
}
