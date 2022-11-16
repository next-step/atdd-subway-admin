package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.LineStations;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineStationResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineStationService {

    private final StationRepository stationRepository;
    private final LineStationRepository lineStationRepository;

    public LineStationService(StationRepository stationRepository, LineStationRepository lineStationRepository) {
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    public List<LineStationResponse> findLineStationsByLineId(Long lineId) {
        List<LineStation> lineStations = lineStationRepository.findByLineId(lineId)
                .orElseThrow(EntityNotFoundException::new);
        return lineStations.stream().map(this::getLineStationResponse).collect(Collectors.toList());
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        LineStations lineStations = createLineStations(lineId);
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(EntityNotFoundException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(EntityNotFoundException::new);
        int distance = sectionRequest.getDistance();

        validateStations(lineStations, upStation, downStation);
        isFindSameUpStationThenCreateNewLineStation(lineStations, upStation, downStation, distance);
        isFindSameDownStationThenCreateNewLineStation(lineStations, upStation, downStation, distance);
    }

    private LineStations createLineStations(Long lineId) {
        LineStations lineStations = new LineStations();
        lineStations.addAll(lineStationRepository.findByLineId(lineId).orElseThrow(EntityNotFoundException::new));
        return lineStations;
    }

    private void validateStations(LineStations lineStations, Station upStation, Station downStation) {
        lineStations.validateAlreadyExistsStation(upStation, downStation);
        lineStations.validateNotExistsStation(upStation, downStation);
    }

    private void isFindSameUpStationThenCreateNewLineStation(LineStations lineStations, Station upStation,
                                                             Station downStation, int distance) {
        addBetweenByUpStation(lineStations, upStation, downStation, distance);
        prependUpStation(lineStations, upStation, downStation, distance);
    }

    private void prependUpStation(LineStations lineStations, Station upStation, Station downStation, int distance) {
        lineStations.findSameUpStation(downStation).ifPresent(lineStation -> lineStationRepository
                .save(lineStation.createNewLineStation(distance, upStation, downStation)));
    }

    private void addBetweenByUpStation(LineStations lineStations, Station upStation, Station downStation,
                                       int distance) {
        lineStations.findSameUpStation(upStation).ifPresent(lineStation -> {
            lineStation.validateLength(distance);
            lineStationRepository.save(lineStation.createNewLineStation(distance, upStation, downStation));
            lineStationRepository.save(lineStation.createNewDownLineStation(distance, downStation));
            lineStationRepository.delete(lineStation);
        });
    }

    private void isFindSameDownStationThenCreateNewLineStation(LineStations lineStations, Station upStation,
                                                               Station downStation, int distance) {
        addBetweenByDownStation(lineStations, upStation, downStation, distance);
        appendDownStation(lineStations, upStation, downStation, distance);
    }

    private void appendDownStation(LineStations lineStations, Station upStation, Station downStation, int distance) {
        lineStations.findSameDownStation(upStation).ifPresent(lineStation -> lineStationRepository
                .save(lineStation.createNewLineStation(distance, upStation, downStation)));
    }

    private void addBetweenByDownStation(LineStations lineStations, Station upStation, Station downStation,
                                         int distance) {
        lineStations.findSameDownStation(downStation).ifPresent(lineStation -> {
            lineStation.validateLength(distance);
            lineStationRepository.save(lineStation.createNewLineStation(distance, upStation, downStation));
            lineStationRepository.save(lineStation.createNewUpLineStation(distance, upStation));
            lineStationRepository.delete(lineStation);
        });
    }

    private LineStationResponse getLineStationResponse(LineStation lineStation) {
        return LineStationResponse.of(lineStation);
    }
}
