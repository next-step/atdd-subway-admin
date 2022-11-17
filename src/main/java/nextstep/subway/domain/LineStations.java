package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.dto.StationResponse;

@Embeddable
public class LineStations {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public LineStations(LineStation lineStation){
        this.lineStations = Collections.singletonList(lineStation);
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
        lineStations.add(lineStation);
    }

    private void validateNotMatchedStation(LineStation lineStation){
        boolean anyMatch = lineStations.stream()
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
        boolean anyMatch = lineStations.stream()
                .anyMatch(it ->
                        it.getUpStation() == lineStation.getUpStation()
                                && it.getDownStation() == lineStation.getDownStation()
                );
        if(anyMatch){
            throw new IllegalArgumentException();
        }
    }

    private void updateUpStation(LineStation lineStation){
        lineStations.stream()
                .filter(it -> it.getUpStation() == lineStation.getUpStation())
                .findFirst()
                .ifPresent(it -> {
                    it.updateUpStation(lineStation.getDownStation());
                    it.updateDistance(lineStation.getDistance());
                });
    }

    private void updateDownStation(LineStation lineStation) {
        lineStations.stream()
                .filter(it -> it.getDownStation() == lineStation.getDownStation())
                .findFirst()
                .ifPresent(it -> {
                    it.updateDownStation(lineStation.getUpStation());
                    it.updateDistance(lineStation.getDistance());
                });
    }

    private boolean isSameUpStation(LineStation lineStation){
        return lineStations.stream()
                .anyMatch(it -> it.getUpStation().equals(lineStation.getUpStation()));
    }

    private boolean isSameDownStation(LineStation lineStation){
        return lineStations.stream()
                .anyMatch(it -> it.getDownStation().equals(lineStation.getDownStation()));
    }

    public List<StationResponse> getLineStations() {
        List<Station> allStations = new ArrayList<>();
        lineStations.forEach(lineStation -> {
            allStations.add(lineStation.getUpStation());
            allStations.add(lineStation.getDownStation());
        });
        return allStations.stream()
                .distinct()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }
}