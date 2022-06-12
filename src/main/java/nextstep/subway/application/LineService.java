package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        Station upStation = stationService.findStationById(lineRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineRequest.getDownStationId());

        Line line = Line.of(lineRequest);
        LineStation lineStation = LineStation.of(upStation, downStation, lineRequest.getDistance());
        line.addLineStation(lineStation);

        return LineResponse.of(saveLine(line));
    }

    private Line saveLine(Line line) {
        return lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new NotFoundException("노선을 찾을 수 없습니다."));
    }

    public LineResponse updateLine(Long lineId, LineRequest lineRequest) {
        Line line = findLineById(lineId);
        line.update(lineRequest.getName(), lineRequest.getColor());

        return LineResponse.of(line);
    }

    public LineResponse getLine(Long lineId) {
        Line line = findLineById(lineId);
        return LineResponse.of(line);
    }

    public void deleteLine(Long lineId) {
        Line line = findLineById(lineId);
        lineRepository.delete(line);
    }

    public void deleteLineStations(Long lineStationId, Long stationId) {
        Line line = this.findLineById(lineStationId);
        Station station = stationService.findStationById(stationId);
        line.deleteLineStations(station);
    }
}
