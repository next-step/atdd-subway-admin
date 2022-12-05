package nextstep.subway.domain.line;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public void add(LineStation lineStation) {
        this.lineStations.add(lineStation);
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public void setLineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public boolean contains(LineStation lineStation) {
        return this.lineStations.contains(lineStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineStations that = (LineStations) o;
        return Objects.equals(lineStations, that.lineStations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineStations);
    }
}
