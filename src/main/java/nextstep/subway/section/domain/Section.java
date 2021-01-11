package nextstep.subway.section.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import nextstep.subway.advice.exception.SectionBadRequestException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Line line;

    private int distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, Line line, int distance) {
        checkDuplicationId(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = distance;
    }

    public Section(Section section) {
        this.upStation = section.getUpStation();
        this.downStation = section.getDownStation();
        this.line = section.getLine();
        this.distance = section.getDistance();
    }

    public Long getId() {
        return id;
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

    public void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void updateDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void updateDistance(int distance) {
        this.distance = distance;
    }

    private void checkDuplicationId(Station upStation, Station downStation) {
        if (upStation.getId() == downStation.getId()) {
            throw new SectionBadRequestException(upStation.getId(), downStation.getId());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        if (upStation != null ? !upStation.equals(section.upStation) : section.upStation != null) return false;
        if (downStation != null ? !downStation.equals(section.downStation) : section.downStation != null) return false;
        return line != null ? line.equals(section.line) : section.line == null;
    }

    @Override
    public int hashCode() {
        int result = upStation != null ? upStation.hashCode() : 0;
        result = 31 * result + (downStation != null ? downStation.hashCode() : 0);
        result = 31 * result + (line != null ? line.hashCode() : 0);
        return result;
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
}
