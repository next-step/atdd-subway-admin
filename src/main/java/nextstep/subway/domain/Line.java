package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.dto.LineEditRequest;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String color;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    protected Line(){}

    public Line(String name, String color, Station preStation, Station station, Integer distance) {
        this(name, color);
        lineStations.add(new LineStation(null, preStation, 0));
        lineStations.add(new LineStation(preStation, station, distance));
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void editLine(LineEditRequest lineEditRequest){
        this.name = lineEditRequest.getName();
        this.color = lineEditRequest.getColor();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public void addSection(Station preStation, Station station, Integer distance) {

        if( isStationPresent(preStation) && isStationPresent(station)){
            throw new IllegalArgumentException("시작/도착 역이 이미 존재합니다.");
        }

        if( !isStationPresent(preStation) && !isStationPresent(station)){
            throw new IllegalArgumentException("시작/도착 역이 모두 존재하지 않습니다.");
        }

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

        this.lineStations.add(new LineStation(preStation, station, distance));
    }

    private boolean isStationPresent(Station station){
        return this.lineStations.stream()
                .anyMatch(lineStation -> lineStation.getStation().equals(station));
    }

    public List<LineStation> getOrderStations(){
        Optional<LineStation> first = this.getLineStations().stream()
                .filter(lineStation -> lineStation.getPreStation() == null)
                .findAny();

        List<LineStation> orders = new ArrayList<>();
        while(first.isPresent()){

            LineStation tmp = first.get();
            orders.add(tmp);
            first = this.getLineStations().stream()
                    .filter(lineStation -> tmp.getStation() == lineStation.getPreStation())
                    .findAny();
        }

        return orders;
    }
}
