package nextstep.subway.dto;

import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

public class LineRequest {

    private String name;
    private String color;
    private int distance;
    private Long upStationId;
    private Long downStationId;
    private Station upStation;
    private Station downStation;

    public LineRequest() {
    }

    public LineRequest(String name, String color, int distance, Long upStationId, Long downStationId) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Line toLine(StationRepository stationRepository) {
        defineUpStation(stationRepository);
        defineDownStation(stationRepository);
        return new Line(name, color, distance, upStation, downStation);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Station getUpStation() {
        return upStation;
    }

    public void defineUpStation(StationRepository stationRepository) {
        this.upStation = stationRepository.findById(getUpStationId()).orElseThrow(EntityNotFoundException::new);
    }

    public Station getDownStation() {
        return downStation;
    }

    public void defineDownStation(StationRepository stationRepository) {
        this.downStation = stationRepository.findById(getDownStationId()).orElseThrow(EntityNotFoundException::new);
    }
}
