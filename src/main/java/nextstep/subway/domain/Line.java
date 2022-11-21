package nextstep.subway.domain;

import nextstep.subway.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @OneToMany(mappedBy = "line")
    private List<LineStation> lineStations = new ArrayList<>();

    private int distance;

    protected Line() {

    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;

        addLineStation(new LineStation(this, upStation));
        addLineStation(new LineStation(this, downStation));
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

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public List<StationResponse> getStationResponses() {
        return lineStations.stream().map(LineStation::getStation)
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void updateNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public int compareToDistance(int distance) {
        return Integer.compare(this.distance, distance);
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.add(lineStation);
    }

    public boolean isContainStation(Station station) {
        return lineStations.stream().anyMatch(lineStation -> station.equals(lineStation.getStation()));
    }
}
