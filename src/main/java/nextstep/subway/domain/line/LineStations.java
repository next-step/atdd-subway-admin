package nextstep.subway.domain.line;

import nextstep.subway.dto.request.LineSectionRequest;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Embeddable
public class LineStations {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    public void addLineStation(LineStation lineStation) {
        lineStations.stream()
                .filter(it -> it.getUpStationId() == lineStation.getUpStationId())
                .findFirst()
                .ifPresent(it -> it.updatePreStationTo(lineStation.getDownStationId(), lineStation.getDistance()));

        this.lineStations.add(lineStation);
    }

    private void modifyExistLineStation(LineStation lineStation) {

    }

    public List<LineStation> getValues() {
        return this.lineStations;
    }
}
