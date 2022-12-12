package nextstep.subway.domain.line;

import nextstep.subway.domain.station.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void addLineStations(LineStations lineStations) {
        for (LineStation lineStation : lineStations.getValues()) {
            this.addLineStation(lineStation);
        }
    }

    public List<LineStation> getValues() {
        return this.lineStations;
    }

    public void removeStation(Station stationToDelete, Line line) {
        checkValidationForUniqueSection();
        addIfRemovePositionMiddleSection(stationToDelete);
        removeStation(getRemoveSections(stationToDelete));
    }

    private void addIfRemovePositionMiddleSection(Station stationToDelete) {
        Optional<LineStation> upSection = getSectionUpLineStation(stationToDelete);
        Optional<LineStation> downSection = getSectionDownLineStation(stationToDelete);

        if (upSection.isPresent() && downSection.isPresent()) {
            LineStation newUpLineStation = upSection.get();
            LineStation newDownLineStation = downSection.get();
            int newDistance = newUpLineStation.getDistance() + newDownLineStation.getDistance();
            this.lineStations.add(new LineStation(newUpLineStation.getUpStationId(), newDownLineStation.getDownStationId(), newDistance));
        }
    }

    private void removeStation(List<LineStation> removeSections) {
        for (LineStation removeSection : removeSections) {
            this.lineStations.remove(removeSection);
        }
    }

    private void checkValidationForUniqueSection() {
        if (this.lineStations.size() <= 1) {
            throw new RuntimeException();
        }
    }

    public Optional<LineStation> getSectionUpLineStation(Station station) {
        return this.lineStations.stream()
                .filter(it -> it.getUpStationId().equals(station.getId()))
                .findFirst();
    }

    public Optional<LineStation> getSectionDownLineStation(Station station) {
        return this.lineStations.stream()
                .filter(it -> it.getDownStationId().equals(station.getId()))
                .findFirst();
    }

    private List<LineStation> getRemoveSections(Station station) {
        return Arrays.asList(getSectionUpLineStation(station), getSectionDownLineStation(station))
                .stream()
                .filter(section -> section.isPresent())
                .map(section -> section.get())
                .collect(Collectors.toList());
    }

    public int size() {
        return this.lineStations.size();
    }
}
