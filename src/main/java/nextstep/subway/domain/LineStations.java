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

    protected LineStations(){

    }

    public void add(Station preStation, Station station, Integer distance){
        this.lineStations.add(new LineStation(preStation, station, distance));
    }

    public void addSection(Station preStation, Station station, Integer distance){
        validateAllIncludeStation(preStation, station);
        validateNotIncludeStation(preStation, station);

        if(isStationPresent(preStation)){
            this.lineStations.stream()
                    .filter(lineStation -> preStation.equals(lineStation.getPreStation()))
                    .findFirst()
                    .ifPresent(lineStation -> lineStation.updateLineStation(station, lineStation.getStation(), distance));
        }

        if(isStationPresent(station)){
            this.lineStations.stream()
                    .filter(lineStation -> station.equals(lineStation.getStation()))
                    .findFirst()
                    .ifPresent(lineStation -> lineStation.updateLineStation(lineStation.getPreStation(), preStation, distance));
        }

        add(preStation, station, distance);
    }

    private void validateNotIncludeStation(Station preStation, Station station) {
        if( !isStationPresent(preStation) && !isStationPresent(station)){
            throw new IllegalArgumentException("시작/도착 역이 모두 존재하지 않습니다.");
        }
    }

    private void validateAllIncludeStation(Station preStation, Station station) {
        if( isStationPresent(preStation) && isStationPresent(station)){
            throw new IllegalArgumentException("시작/도착 역이 이미 존재합니다.");
        }
    }

    private boolean isStationPresent(Station station){
        return this.lineStations.stream()
                .anyMatch(lineStation -> station.equals(lineStation.getStation()));
    }

    public List<LineStation> getOrderStations(){
        Optional<LineStation> first = this.lineStations.stream()
                .filter(lineStation -> lineStation.getPreStation() == null)
                .findAny();

        List<LineStation> orders = new ArrayList<>();
        while(first.isPresent()){
            LineStation tmp = first.get();
            orders.add(tmp);
            first = this.lineStations.stream()
                    .filter(lineStation -> tmp.getStation() == lineStation.getPreStation())
                    .findAny();
        }
        return orders;
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }
}
