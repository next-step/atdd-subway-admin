package nextstep.subway.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.exception.ConflictException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @OrderBy("id ASC")
    private List<LineStation> lineStations;

    protected LineStations() {
        lineStations = new ArrayList<>();
    }

    public static LineStations empty() {
        return new LineStations();
    }

    public void addLineStation(Line line, LineStation newLineStation) {
        validateCreationLineStation(newLineStation);
        lineStations.forEach(lineStation -> {
            updateUpStation(newLineStation, lineStation);
            updateDownStation(newLineStation, lineStation);
        });

        newLineStation.toLine(line);
        lineStations.add(newLineStation);
    }

    private void updateUpStation(LineStation newLineStation, LineStation lineStation) {
        if (lineStation.getUpStation().isSame(newLineStation.getUpStation())) {
            lineStation.updateUpStation(newLineStation);
        }
    }

    private void updateDownStation(LineStation newLineStation, LineStation lineStation) {
        if (lineStation.getDownStation().isSame(newLineStation.getDownStation())) {
            lineStation.downUpStation(newLineStation);
        }
    }

    private void validateCreationLineStation(LineStation newLineStation) {
        if (lineStations.size() > 0) {
            validateOverlapLineStation(newLineStation);
            validateNotContainStation(newLineStation);
        }
    }

    private void validateOverlapLineStation(LineStation newLineStation) {
        if (lineStations.contains(newLineStation)) {
            System.out.println("상행역과 하행역이 이미 노선에 모두 등록되어있습니다.");
            throw new ConflictException("상행역과 하행역이 이미 노선에 모두 등록되어있습니다.");
        }
    }

    private void validateNotContainStation(LineStation newLineStation) {
        List<Station> allStations = getStationsInLineStations();

        if (
                !allStations.contains(newLineStation.getUpStation()) &&
                        !allStations.contains(newLineStation.getDownStation())
        ) {
            System.out.println("새로운 구간의 역이 상행역과 하행역 둘 중 하나에 포함되어야 합니다.");
            throw new BadRequestException("새로운 구간의 역이 상행역과 하행역 둘 중 하나에 포함되어야 합니다.");
        }
    }

    public List<Station> getStationsInLineStations() {
        List<Station> stations = lineStations.stream()
                .map(LineStation::getStations)
                .collect(Collectors.toList())
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return stations.stream().distinct().sorted(Comparator.comparing(Station::getCreatedDate)).collect(Collectors.toList());
    }

    public void deleteLineStations(Line line, Station station) {
        validDeleteStation();

        Optional<LineStation> deletionTargetTopLineStation = findTopStationInLineStation(station);
        Optional<LineStation> deletionTargetDownLineStation = findDownStationInLineStation(station);

        deletionTargetTopLineStation.ifPresent(lineStation -> {
            this.deleteLineStation(lineStation);
            lineStation.deleteLine();
        });

        deletionTargetDownLineStation.ifPresent(lineStation -> {
            this.deleteLineStation(lineStation);
            lineStation.deleteLine();
        });

        if (
                deletionTargetTopLineStation.isPresent() && deletionTargetDownLineStation.isPresent()
        ) {
            LineStation lineStation = LineStation.of(
                    deletionTargetTopLineStation.get().getDownStation(),
                    deletionTargetDownLineStation.get().getUpStation(),
                    deletionTargetTopLineStation.get().getDistance() + deletionTargetDownLineStation.get().getDistance()
            );

            this.addLineStation(line, lineStation);
        }
    }

    private void deleteLineStation(LineStation lineStation) {
        this.lineStations.remove(lineStation);
    }

    private Optional<LineStation> findTopStationInLineStation(Station topStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getUpStation().isSame(topStation))
                .findFirst();
    }

    private Optional<LineStation> findDownStationInLineStation(Station topStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getDownStation().isSame(topStation))
                .findFirst();
    }

    private void validDeleteStation() {
        if (lineStations.size() == 1) {
            System.out.println("구간이 하나인 노선은 역을 삭제할 수 없습니다.");
            throw new BadRequestException("구간이 하나인 노선은 역을 삭제할 수 없습니다.");
        }
    }

    public List<LineStation> get() {
        return lineStations;
    }

    private int lineStationCount() {
        return lineStations.size();
    }

    @Override
    public String toString() {
        return "LineStations{" +
                "lineStations=" + lineStations +
                '}';
    }
}
