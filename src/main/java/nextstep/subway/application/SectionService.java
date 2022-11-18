package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStations;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.EntityNotFoundException;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.LineStationRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SectionService {

    private LineRepository lineRepository;

    private StationRepository stationRepository;

    private LineStationRepository lineStationRepository;

    public SectionService(LineRepository lineRepository,
                          StationRepository stationRepository,
                          LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    @Transactional
    public LineResponse saveSection(SectionRequest sectionRequest, Long lineId) {
        Line line = getLine(lineId).orElseThrow(() -> new EntityNotFoundException("Line", lineId));
        LineStations lineStations = line.getLineStations();

        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();

        Station upStation = getStation(upStationId)
                .orElseThrow(() -> new EntityNotFoundException("Station", upStationId));
        Station downStation = getStation(downStationId)
                .orElseThrow(() -> new EntityNotFoundException("Station", downStationId));

        LineStation lineStation = lineStationRepository
                .save(new LineStation(upStation, downStation, sectionRequest.getDistance()));

        lineStations.infixSection(lineStation);

        return LineResponse.of(line);
    }

    private Optional<Line> getLine(Long id) {
        return lineRepository.findById(id);
    }

    private Optional<Station> getStation(Long id) {
        return stationRepository.findById(id);
    }

}
