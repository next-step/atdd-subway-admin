package nextstep.subway.dto;

import java.util.Optional;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.repository.StationRepository;

public class LineRequest {
    private final StationRepository stationRepository;
    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public LineRequest(StationRepository stationRepository, String name, String color, Long upStationId,
                       Long downStationId, int distance) {
        this.stationRepository = stationRepository;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toLine() {
        Optional<Station> upStation = stationRepository.findById(upStationId);
        Optional<Station> downStation = stationRepository.findById(downStationId);
        return Line.builder(name, color, upStation.orElseThrow(NotFoundException::new),
                downStation.orElseThrow(NotFoundException::new), distance).build();
    }
}
