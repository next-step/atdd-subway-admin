package nextstep.subway.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());
        Line line = lineRepository.save(Line.of(lineRequest, upStation, downStation));

        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findLineAndStations();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findLineAndStationsById(id)
            .orElseThrow(() -> new NoSuchElementException("지하철 노선을 찾을 수 없습니다."));

        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("지하철 노선을 찾을 수 없습니다."));
        line.update(lineRequest);

        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("지하철 노선을 찾을 수 없습니다."));

        lineRepository.delete(line);
    }
}
