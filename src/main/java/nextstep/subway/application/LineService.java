package nextstep.subway.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.StationResponse;
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
        Line saveLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(saveLine, StationResponse.of(upStation), StationResponse.of(downStation));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line,
                        StationResponse.of(line.getUpStation()),
                        StationResponse.of(line.getDownStation())))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Optional<Line> findLine = lineRepository.findById(id);
        return findLine.map(line -> LineResponse.of(line,
                        StationResponse.of(line.getUpStation()),
                        StationResponse.of(line.getDownStation())))
                .orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public void updateLineById(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        line.updateLine(new Line(lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
