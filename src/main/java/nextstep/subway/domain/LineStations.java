package nextstep.subway.domain;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashSet;
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
        if (lineStations.isEmpty()) {
            lineStations.add(newLineStation);
            return;
        }

        validate(newLineStation);
        lineStations.forEach(lineStation -> lineStation.update(newLineStation));
        lineStations.add(newLineStation);
    }

    private void validate(LineStation lineStation) {
        validateHasStations(lineStation);
    }

    private void validateHasStations(LineStation newLineStation) {
        if (new HashSet<>(getStations()).containsAll(newLineStation.findStations())) {
            throw new IllegalArgumentException("등록하려는 역이 모두 존재합니다.");
        }
    }
}
