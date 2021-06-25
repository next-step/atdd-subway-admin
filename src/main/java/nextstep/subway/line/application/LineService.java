package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    @Autowired
    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(this.lineRequestToLine(request));
        return LineResponse.of(persistLine);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
        this.lineRepository.save(line);
    }

    private Line lineRequestToLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        line.addSections(this.stationService.findStation(lineRequest.getUpStationId())
            , this.stationService.findStation(lineRequest.getDownStationId())
            , lineRequest.getDistance());
        return line;
    }

    public List<LineResponse> findAllLines() {
        return this.lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public Optional<LineResponse> findLine(Long id) {
        return this.lineRepository.findById(id).map(LineResponse::of);
    }

    public void deleteLine(Long id) {
        this.lineRepository.delete(findLineById(id));
    }

    private Line findLineById(Long id) {
        return this.lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("cannot find line."));
    }
}