package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "section")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "upstation_id")
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "downstation_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {

    }

    public Section(Station upStation, Station downStation, long distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public List<Station> getLineStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setLine(final Line line) {
        if (Objects.nonNull(this.line)) {
            this.line.getSections().remove(this);
        }
        this.line = line;
        line.getSections().add(this);
    }

    private void invalidSectionCheck() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(
                upStation, section.upStation) && Objects.equals(downStation, section.downStation)
                && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, distance, line);
    }
}
