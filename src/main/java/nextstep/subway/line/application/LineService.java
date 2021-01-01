package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStations;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.line.dto.SectionCreateRequest;
import nextstep.subway.line.dto.SectionCreateResponse;
import nextstep.subway.line.exception.AlreadySavedLineException;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(StationService stationService,
            LineRepository lineRepository) {
        this.stationService = stationService;
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
        Line savedLine = findByIdWithLineStation(id);
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

    public SectionCreateResponse addSection(Long id, SectionCreateRequest request) {
        Line savedLine = findByIdWithLineStation(id);
        LineStations stations = savedLine.getLineStations();

        List<Long> ids = stations.getLineStations().stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());

        stationService.cacheByIds(ids);

        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        LineStation lineStation = new LineStation(savedLine, downStation, upStation, request.getDistance());
        stations.add(lineStation);

        return SectionCreateResponse.of(savedLine);
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

    private Line findByIdWithLineStation(Long id) {
        return lineRepository.findByIdWithLineStation(id)
                .orElseThrow(LineNotFoundException::new);
    }

}
