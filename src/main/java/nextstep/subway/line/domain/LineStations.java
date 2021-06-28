package nextstep.subway.line.domain;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public LineStations(LineStation upStation, LineStation downStation) {
        this.lineStations.addAll(Arrays.asList(upStation, downStation));
    }

    public List<LineStation> getStationsOrdered() {
        Optional<LineStation> preStation = getFirstLineStation();
        List<LineStation> stations = new ArrayList<>();
        while (preStation.isPresent()) {
            stations.add(preStation.get());
            preStation = findNext(preStation.get().getStationId());
        }
        return stations;
    }

    private Optional<LineStation> findNext(Long upStationId) {
        return this.lineStations.stream().filter(lineStation -> lineStation.hasSameUpStation(upStationId)).findAny();
    }

    public List<Long> getStationIds() {
        return this.lineStations.stream().map(LineStation::getStationId).collect(Collectors.toList());
    }

    private Optional<LineStation> getFirstLineStation() {
        return this.lineStations.stream().filter(station -> station.getUpStationId() == null).findAny();
    }

    public void add(LineStation lineStation) {
        this.lineStations.stream()
                .filter(station -> station.getUpStationId() == lineStation.getUpStationId())
                .findFirst()
                .ifPresent(station -> {
                    validate(station, lineStation);
                    station.changeUpStation(lineStation.getStationId(), -lineStation.getDistance());
                });
        this.lineStations.add(lineStation);
    }

    public Optional<LineStation> getSameLineStation(long id) {
        return this.lineStations.stream()
                .filter(station -> station.isSameStation(id))
                .findFirst();
    }

    public void delete(Long lineStationId) {
        if (this.lineStations.size() < 3) {
            throw new IllegalStateException("노선의 마지막 남은 구간은 삭제할 수 없습니다.");
        }
        LineStation deletingStation = this.lineStations.stream()
                .filter(lineStation -> lineStation.isSameStation(lineStationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("없는 구간 정보 입니다."));
        this.lineStations.stream()
                .filter(lineStation -> lineStation.getUpStationId() == lineStationId)
                .findFirst()
                .ifPresent(lineStation -> lineStation.changeUpStation(deletingStation.getUpStationId(), deletingStation.getDistance()));
        this.lineStations.remove(deletingStation);
    }

    private void validate(LineStation existedStation, LineStation addedStation) {
        if (existedStation.getDistance() != 0 && existedStation.getDistance() <= addedStation.getDistance()) {
            throw new IllegalArgumentException("구간 거리가 기존역 사이보다 크거나 같습니다.");
        }
        if (existedStation.isSameStation(addedStation.getStationId())) {
            throw new IllegalArgumentException("이미 등록되어있는 구간입니다.");
        }
    }
}
