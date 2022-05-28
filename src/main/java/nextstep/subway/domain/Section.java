package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
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
    private Station upStationId;

    @OneToOne
    @JoinColumn(name = "downstation_id")
    private Station downStationId;

    @Column
    private long distance;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {

    }

    public Section(Station upStationId, Station downStationId, long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public List<Station> getLineStations() {
        return Arrays.asList(upStationId, downStationId);
    }

    public Station getUpStationId() {
        return upStationId;
    }

    public Station getDownStationId() {
        return downStationId;
    }

    public void setLine(final Line line) {
        if (Objects.nonNull(this.line)) {
            this.line.getSections().remove(this);
        }
        this.line = line;
        line.getSections().add(this);
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
                upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId)
                && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance, line);
    }
}
