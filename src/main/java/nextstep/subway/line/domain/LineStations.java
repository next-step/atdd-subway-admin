package nextstep.subway.line.domain;

import nextstep.subway.line.domain.LineStation;

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
                    station.changeUpStation(lineStation.getStationId());
                });
        this.lineStations.add(lineStation);
    }

    public Optional<LineStation> getSameLineStation(long id){
        return this.lineStations.stream()
                .filter(station -> station.isSameStation(id))
                .findFirst();
    }

    private void validate(LineStation existedStation, LineStation addedStation) {
        if (existedStation.getDistance() != null && existedStation.getDistance() <= addedStation.getDistance()) {
            throw new IllegalArgumentException("구간 거리가 기존역 사이보다 크거나 같습니다.");
        }
        if (existedStation.isSameStation(addedStation.getStationId())) {
            throw new IllegalArgumentException("이미 등록되어있는 구간입니다.");
        }
    }
}
