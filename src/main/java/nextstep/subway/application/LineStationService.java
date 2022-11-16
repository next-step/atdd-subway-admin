package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.LineStations;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineStationResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
        LineStations lineStations = new LineStations();
        lineStations.addAll(lineStationRepository.findByLineId(lineId).orElseThrow(EntityNotFoundException::new));

        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(EntityNotFoundException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(EntityNotFoundException::new);
        int distance = sectionRequest.getDistance();
        isFindSameUpStationThenCreateNewLineStation(lineStations, upStation, downStation, distance);
        isFindSameDownStationThenCreateNewLineStation(lineStations, upStation, downStation, distance);
    }

    private void isFindSameUpStationThenCreateNewLineStation(LineStations lineStations, Station upStation,
                                                             Station downStation, int distance) {
        lineStations.findSameUpStation(upStation).ifPresent(lineStation -> {
            lineStationRepository.save(lineStation.createNewLineStation(distance, upStation, downStation));
            lineStationRepository.save(lineStation.createNewDownLineStation(distance, downStation));
            lineStationRepository.delete(lineStation);
        });
    }

    private void isFindSameDownStationThenCreateNewLineStation(LineStations lineStations, Station upStation,
                                                               Station downStation, int distance) {
        lineStations.findSameDownStation(downStation).ifPresent(lineStation -> {
            lineStationRepository.save(lineStation.createNewLineStation(distance, upStation, downStation));
            lineStationRepository.save(lineStation.createNewUpLineStation(distance, upStation));
            lineStationRepository.delete(lineStation);
        });
    }

    private LineStationResponse getLineStationResponse(LineStation lineStation) {
        return LineStationResponse.of(lineStation);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
