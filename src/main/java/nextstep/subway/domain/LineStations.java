package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    private List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {}

    public List<LineStation> getLineStations() {
        return lineStations.stream().sorted().collect(Collectors.toList());
    }

    public void add(LineStation lineStation) {
        lineStations.add(lineStation);
    }
}
