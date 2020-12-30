package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.line.exception.AlreadySavedLineException;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(StationRepository stationRepository,
            LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    @Transactional(readOnly = true)
    public List<LinesResponse> findAll() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LinesResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line savedLine = lineRepository.findByIdWithLineStation(id)
                .orElseThrow(LineNotFoundException::new);
        return LineResponse.of(savedLine);
    }

    public LineResponse saveLine(LineRequest request) {
        Optional<Line> line = lineRepository.findByNameWithLineStation(request.getName());
        if (line.isPresent()) {
            throw new AlreadySavedLineException();
        }
        return persistLine(request);
    }

    public void updateLine(Long id, LineUpdateRequest request) {
        Line savedLine = lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
        savedLine.update(request.getName(), request.getColor());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private LineResponse persistLine(LineRequest request) {
        Line line = lineRepository.save(request.toLine());
        createNewLineStations(
                line, request.getUpStationId(),
                request.getDownStationId(), request.getDistance())
                .forEach(line::add);
        return LineResponse.of(line);
    }

    private List<LineStation> createNewLineStations(Line line, Long upStationId, Long downStationId, long distance) {
        Station upStation = stationRepository.findById(upStationId)
                .orElseThrow(StationNotFoundException::new);
        Station downStation = stationRepository.findById(downStationId)
                .orElseThrow(StationNotFoundException::new);
        return Arrays.asList(
                new LineStation(line, upStation, null, 0),
                new LineStation(line, downStation, upStation, distance)
        );
    }

}
