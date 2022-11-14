package nextstep.subway.domain;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class LineStations {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<LineStation> lineStations = Lists.newArrayList();

    protected LineStations() {
    }

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public List<Station> getStations() {
        return lineStations.stream()
                .map(LineStation::findStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
    public static LineStations create() {
        return new LineStations();
    }

    public void add(LineStation newLineStation) {
        lineStations.forEach(lineStation -> lineStation.update(newLineStation));
        lineStations.add(newLineStation);
    }
}
