package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
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

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());
        Line persistLine = lineRepository.save(
            new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance()));
        List<StationResponse> stations = getStationResponses(persistLine);
        return LineResponse.of(persistLine, stations);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(line -> {
                List<StationResponse> stationResponses = getStationResponses(line);
                return LineResponse.of(line, stationResponses);
            })
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        Line line = findLineById(lineId);
        List<StationResponse> stationResponses = getStationResponses(line);
        return LineResponse.of(line, stationResponses);
    }

    public LineResponse update(Long lineId, LineRequest lineRequest) {
        Line lineById = findLineById(lineId);
        lineById.update(lineRequest.toLine());
        List<StationResponse> stationResponses = getStationResponses(lineById);
        return LineResponse.of(lineById, stationResponses);
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 LINE 입니다."));
    }

    public Long delete(Long lineId) {
        Line lineById = findLineById(lineId);
        lineRepository.delete(lineById);
        return lineById.getId();
    }

    private List<StationResponse> getStationResponses(Line persistLine) {
        List<LineStation> stations = persistLine.getStations();
        List<StationResponse> collect = stations.stream()
            .map(lineStation -> StationResponse.of(lineStation.getDownStation()))
            .collect(Collectors.toList());

        List<StationResponse> collect2 = stations.stream()
            .map(lineStation -> StationResponse.of(lineStation.getUpStation()))
            .collect(Collectors.toList());
        return Stream.concat(collect.stream(), collect2.stream()).distinct().collect(Collectors.toList());
    }
}
