package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationResponse;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String color;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> stations = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        stations.add(new LineStation(this, upStation, downStation, distance));
    }

    public void updateLine(LineRequest request){
        this.name = request.getName();
        this.color = request.getColor();
    }

    public void addLineStation(LineStation lineStation) {
        validateDuplicated(lineStation);
        validateNotMatchedStation(lineStation);
        if(isSameUpStation(lineStation)){
            updateUpStation(lineStation);
        }
        if(isSameDownStation(lineStation)){
            updateDownStation(lineStation);
        }
        stations.add(lineStation);
    }

    private void validateNotMatchedStation(LineStation lineStation){
        boolean anyMatch = stations.stream()
                .anyMatch(it ->
                       it.getUpStation() == lineStation.getUpStation()
                    || it.getUpStation() == lineStation.getDownStation()
                    || it.getDownStation() == lineStation.getUpStation()
                    || it.getDownStation() == lineStation.getDownStation()
                );
        if(!anyMatch){
            throw new IllegalArgumentException();
        }
    }

    private void validateDuplicated(LineStation lineStation){
        boolean anyMatch = stations.stream()
                .anyMatch(it ->
                       it.getUpStation() == lineStation.getUpStation()
                    && it.getDownStation() == lineStation.getDownStation()
                );
        if(anyMatch){
            throw new IllegalArgumentException();
        }
    }

    private void updateUpStation(LineStation lineStation){
        stations.stream()
                .filter(it -> it.getUpStation() == lineStation.getUpStation())
                .findFirst()
                .ifPresent(it -> {
                    it.updateUpStation(lineStation.getDownStation());
                    it.updateDistance(lineStation.getDistance());
                });
    }

    private void updateDownStation(LineStation lineStation) {
        stations.stream()
                .filter(it -> it.getDownStation() == lineStation.getDownStation())
                .findFirst()
                .ifPresent(it -> {
                    it.updateDownStation(lineStation.getUpStation());
                    it.updateDistance(lineStation.getDistance());
                });
    }

    private boolean isSameUpStation(LineStation lineStation){
        return stations.stream()
                .anyMatch(it -> it.getUpStation().equals(lineStation.getUpStation()));
    }

    private boolean isSameDownStation(LineStation lineStation){
        return stations.stream()
                .anyMatch(it -> it.getDownStation().equals(lineStation.getDownStation()));
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

    public List<StationResponse> getStations() {
        List<Station> allStations = new ArrayList<>();
        stations.forEach(lineStation -> {
            allStations.add(lineStation.getUpStation());
            allStations.add(lineStation.getDownStation());
        });
        return allStations.stream()
                .distinct()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", stations=" + stations +
                '}';
    }
}
