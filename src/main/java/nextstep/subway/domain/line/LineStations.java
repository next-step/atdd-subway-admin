package nextstep.subway.domain.line;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {}

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void addLineStation(LineStation lineStation) {
        checkValidation(lineStation);

        lineStations.stream()
                .filter(it -> it.getUpStationId() == lineStation.getUpStationId())
                .findFirst()
                .ifPresent(it -> it.updatePreStationTo(lineStation.getDownStationId(), lineStation.getDistance()));

        this.lineStations.add(lineStation);
    }

    private void checkValidation(LineStation lineStation) {
        for (LineStation existLineStation : lineStations) {
            validationCheckSame(existLineStation, lineStation);
            validationCheckDistance(existLineStation, lineStation);
        }

        validationCheckForStation(lineStation);
    }

    private void validationCheckForStation(LineStation lineStation) {
        if (lineStations.size() > 0) {
            checkForIfHasStationToAdd(lineStation);
        }
    }

    private void checkForIfHasStationToAdd(LineStation lineStation) {
        lineStations.stream()
                .filter(it ->
                        it.getUpStationId() == lineStation.getUpStationId() || it.getUpStationId() == lineStation.getDownStationId() ||
                                it.getDownStationId() == lineStation.getUpStationId() || it.getDownStationId() == lineStation.getDownStationId()
                )
                .findFirst()
                .orElseThrow(() -> new RuntimeException());
    }

    private void validationCheckDistance(LineStation existLineStation, LineStation lineStation) {
        if ( existLineStation.getUpStationId() == lineStation.getUpStationId() && existLineStation.getDistance() <= lineStation.getDistance() ) {
            throw new RuntimeException();
        }
    }

    private void validationCheckSame(LineStation existLineStation, LineStation lineStation) {
        if ( existLineStation.isSame(lineStation) ) {
            throw new RuntimeException();
        }
    }

    public List<LineStation> getValues() {
        return this.lineStations;
    }
}
