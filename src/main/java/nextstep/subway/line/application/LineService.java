package nextstep.subway.line.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;

    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = request.toLine();
        validateDuplicateLine(line);

        Station station = stationService.findStation(request.getUpStationId());
        Station nextStation = stationService.findStation(request.getDownStationId());

        LineStation lineStation = LineStation.of(station.getId(), nextStation.getId(),
            Distance.of(request.getDistance()));
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

        Map<Long, Station> stations = stationService.findAllById(stationIds);
        List<LineStationResponse> lineStationResponses = extractLineStationResponses(
            line.getStations(), stations);

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
    protected List<LineStationResponse> extractLineStationResponses(List<LineStation> lineStations,
        Map<Long, Station> stations) {
        List<LineStationResponse> result = new ArrayList<>();
        for (LineStation lineStation : lineStations) {
            Station station = stations.get(lineStation.getStationId());
            result.add(LineStationResponse.of(StationResponse.of(station), lineStation));
        }

        return result;
    }
}
