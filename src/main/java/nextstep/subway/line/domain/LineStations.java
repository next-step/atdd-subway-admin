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

    protected LineStations(){}

    public LineStations(LineStation upStation, LineStation downStation){
        this.lineStations.addAll(Arrays.asList(upStation, downStation));
    }

    public List<LineStation> getStationsOrdered(){
        Optional<LineStation> preStation = getFirstLineStation();
        List<LineStation> stations = new ArrayList<>();
        while(preStation.isPresent()){
            stations.add(preStation.get());
            preStation = findNext(preStation.get().getStationId());
        }
        return stations;
    }

    private Optional<LineStation> findNext(long upStationId){
        return this.lineStations.stream().filter(lineStation -> lineStation.hasSameUpStation(upStationId)).findAny();
    }

    public List<Long> getStationIds(){
        return this.lineStations.stream().map(LineStation::getStationId).collect(Collectors.toList());
    }

    private Optional<LineStation> getFirstLineStation(){
        return this.lineStations.stream().filter(station -> station.getUpStationId() == null).findAny();
    }

    public void add(LineStation lineStation){
        this.lineStations.stream()
                .filter(station -> station.getUpStationId() == lineStation.getUpStationId())
                .findFirst()
                .ifPresent(station -> station.changeUpStation(lineStation.getStationId()));
        this.lineStations.add(lineStation);
    }
}
