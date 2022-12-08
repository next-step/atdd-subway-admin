package nextstep.subway.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.constants.ErrorMessage;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.line.LineStation;
import nextstep.subway.domain.line.LineStationRepository;
import nextstep.subway.domain.line.LineStations;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private static final int MIN_LINE_STATION_COUNT = 1;
    private LineRepository lineRepository;
    private LineStationRepository lineStationRepository;
    private StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, LineStationRepository lineStationRepository,
            StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public SectionResponse saveSection(Long lineId, SectionRequest sectionRequest) {
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(EntityNotFoundException::new);
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(EntityNotFoundException::new);
        Line line = lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new);

        LineStation lineStation = new LineStation(downStation, upStation, sectionRequest.getDistance(), line);
        line.addLineStation(lineStation);
        return SectionResponse.of(lineStationRepository.save(lineStation));
    }

    public List<SectionResponse> findLineStationsByLineId(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        LineStations findResults = new LineStations();
        findResults.addAll(lineStationRepository.findByLine(line));
        return findResults.stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }

    public List<SectionResponse> findLineStationByStationId(Long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(NoSuchElementException::new);
        LineStations findResults = new LineStations();
        findResults.addAll(lineStationRepository.findByStation(station));
        findResults.addAll(lineStationRepository.findByPreStation(station));
        return findResults.stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        checkLineStationDeleteAble(line);

        Station station = stationRepository.findById(stationId).orElseThrow(EntityNotFoundException::new);
        LineStations findResults = findByStationAndLine(station, line);
        lineStationRepository.deleteAllInBatch(findResults.getLineStations());

        LineStation reAssignedLineStation = findResults.reAssignLineStation(station, line);
        if (reAssignedLineStation != null) {
            lineStationRepository.save(reAssignedLineStation);
        }
    }

    private void checkLineStationDeleteAble(Line line) {
        if (lineStationRepository.countByLine(line) <= MIN_LINE_STATION_COUNT) {
            throw new IllegalArgumentException(ErrorMessage.LAST_LINE_STATION_CANNOT_BE_DELETED);
        }
    }

    private LineStations findByStationAndLine(Station station, Line line) {
        LineStations findResults = new LineStations(lineStationRepository.findByStationAndLine(station, line));
        findResults.checkLineStationExist();
        return findResults;
    }
}
