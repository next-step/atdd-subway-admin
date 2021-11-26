package nextstep.subway.line.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.DuplicateException;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
        StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = request.toLine();
        validateDuplicateLine(line);

        LineStation lineStation = LineStation.of(request.getUpStationId(),
            request.getDownStationId(), Distance.of(request.getDistance()));
        line.addLineStation(lineStation);

        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line persistLine = findLine(id);

        persistLine.update(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findByLineId(Long id) {
        Line line = findLine(id);
        List<Long> stationIds = line.getStations().stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());

        Map<Long, Station> stations = stationRepository.findAllById(stationIds).stream()
            .collect(Collectors.toMap(Station::getId, Function.identity()));

        List<LineStationResponse> lineStationResponses = extractLineStationResponses(line,
            stations);

        return LineResponse.of(line, lineStationResponses);
    }

    public void deleteLineById(Long id) {
        Line line = findLine(id);
        line.delete();

        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    protected Line findLine(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NotFoundException::new);
    }

    @Transactional(readOnly = true)
    protected void validateDuplicateLine(Line line) {
        Optional<Line> optionalLine = lineRepository.findByName(line.getName());

        optionalLine.ifPresent(findLine -> {
            throw new DuplicateException("이미 등록된 노선 이름을 사용할 수 없습니다.");
        });
    }

    @Transactional(readOnly = true)
    protected List<LineStationResponse> extractLineStationResponses(Line line,
        Map<Long, Station> stations) {
        return line.getStations().stream()
            .map(it -> LineStationResponse.of(it,
                StationResponse.of(stations.get(it.getStationId()))))
            .collect(Collectors.toList());
    }
}
