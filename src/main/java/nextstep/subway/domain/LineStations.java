package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class LineStations {
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    public void init(Long upStationId, Long downStationId, Integer distance) {
        lineStations.add(LineStation.of(null, upStationId, null));
        lineStations.add(LineStation.of(upStationId, downStationId, distance));
    }

    public void remove() {
        lineStations.clear();
    }

    public List<LineStation> getStationsInOrder() {
        Optional<LineStation> optionalPreLineStation = findFirst(null);

        List<LineStation> result = new ArrayList<>();
        while (optionalPreLineStation.isPresent()) {
            LineStation preLineStation = optionalPreLineStation.get();
            result.add(preLineStation);
            optionalPreLineStation = findFirst(preLineStation.getStation().getId());
        }
        return result;
    }

    private Optional<LineStation> findFirst(Long preStationId) {
        return lineStations.stream()
            .filter(lineStation -> lineStation.getPreStationId() == preStationId)
            .findFirst();
    }
}
