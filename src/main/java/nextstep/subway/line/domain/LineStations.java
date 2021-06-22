package nextstep.subway.line.domain;

import nextstep.subway.line.domain.LineStation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public void addLineStation(LineStation lineStation){
        this.lineStations.add(lineStation);
    }

    public List<LineStation> getStationsOrdered(){
        return this.lineStations;
    }

    public List<Long> getStationIds(){
        return this.lineStations.stream().map(LineStation::getStationId).collect(Collectors.toList());
    }

}
