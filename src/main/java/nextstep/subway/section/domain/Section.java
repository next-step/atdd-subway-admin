package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.application.SectionDistance;
import nextstep.subway.station.domain.Station;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Optional;

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

    @Embedded
    private SectionDistance distance;
    private boolean start;

    protected Section() {
    }

    public Section(Line line, Station up, Station down, int distance, boolean start) {
        super();
        this.line = line;
        this.up = up;
        this.down = down;
        this.distance = new SectionDistance(distance);
        this.start = start;
    }

    public Section(Line line, Station up, Station down, int distance) {
        super();
        this.line = line;
        this.up = up;
        this.down = down;
        this.distance = new SectionDistance(distance);
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance, boolean start) {
        return new Section(line, upStation, downStation, distance, start);
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
        return this.distance.getValue();
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
        Section result = new Section(this.line, up, down, this.distance.getValue() - distance);
        this.distance = new SectionDistance(this.distance.getValue() - distance);
        this.up = down;
        return result;
    }

    private Section getNewStartSection(Station up, Station down, int distance) {
        Section result = new Section(this.line, up, down, this.distance.getValue() - distance, true);
        this.distance = new SectionDistance(this.distance.getValue() - distance);
        this.up = down;
        this.start = false;
        return result;
    }

    private Section getMiddleToDownSection(Station up, Station down, int distance) {
        checkDistance(distance);
        Section result = new Section(this.line, up, down, this.distance.getValue() - distance);
        this.distance = new SectionDistance(this.distance.getValue() - distance);
        this.down = up;
        return result;
    }

    private void checkDistance(int distance) {
        if (this.distance.getValue() <= distance) {
            throw new IllegalArgumentException("distance is invalid");
        }
    }


    public boolean getStart() {
        return this.start;
    }

    public void mergeUp(Section section) {
        if (section.start) {
            this.start = true;
        }
        this.distance = new SectionDistance(section.getDistance() + this.distance.getValue());
        this.up = section.getUp();
    }

    public void setStart() {
        this.start = true;
    }
}
