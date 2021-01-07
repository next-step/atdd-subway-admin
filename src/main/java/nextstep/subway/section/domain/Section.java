package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up")
    private Station up;

    @ManyToOne
    @JoinColumn(name = "down")
    private Station down;

    @ManyToOne
    @JoinColumn(name = "line")
    private Line line;

    private Integer distance;
    private boolean start;

    protected Section() {
    }

    public Section(Line line, Station up, Station down, int distance, boolean start) {
        super();
        this.line = line;
        this.up = up;
        this.down = down;
        this.distance = distance;
        this.start = start;
    }

    public Section(Line line, Station up, Station down, int distance) {
        super();
        this.line = line;
        this.up = up;
        this.down = down;
        this.distance = distance;
    }

    public Long getId() {
        return this.id;
    }

    public Station getUp() {
        return this.up;
    }

    public Station getDown() {
        return this.down;
    }

    public int getDistance() {
        return this.distance;
    }

    public void setNotStart() {
        this.start = false;
    }

    public Section createAndChange(Station up, Station down, int distance) {
        if (this.down == (down)) {
            return getMiddleToDownSection(up, down, distance);
        }
        if (this.up == (up)) {
            checkDistance(distance);
            if (this.start) {
                return getNewStartSection(up, down, distance);
            }
            return getUpToMiddleSection(up, down, distance);
        }
        return null;
    }

    private Section getUpToMiddleSection(Station up, Station down, int distance) {
        Section result = new Section(this.line, up, down, this.distance - distance);
        this.distance -= distance;
        this.up = down;
        return result;
    }

    private Section getNewStartSection(Station up, Station down, int distance) {
        Section result = new Section(this.line, up, down, this.distance - distance, true);
        this.distance -= distance;
        this.up = down;
        this.start = false;
        return result;
    }

    private Section getMiddleToDownSection(Station up, Station down, int distance) {
        checkDistance(distance);
        Section result = new Section(this.line, up, down, this.distance - distance);
        this.distance -= distance;
        this.down = up;
        return result;
    }

    private void checkDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("distance is invalid");
        }
    }


    public boolean getStart() {
        return this.start;
    }
}
