package nextstep.subway.application;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineColor;
import nextstep.subway.domain.line.LineName;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.exception.NotFoundEntityException;
import nextstep.subway.repository.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public LineResponse saveLine(LineCreateRequest request) {
        Line line = lineRepository.save(mapToLine(request));
        return LineResponse.of(line);
    }

    private Line mapToLine(LineCreateRequest request) {
        Line line = request.toLineEntity();
        line.toUpStation(stationService.findById(request.getUpStationId()));
        line.toDownStation(stationService.findById(request.getDownStationId()));
        return line;
    }

    @Transactional
    public LineResponse updateLine(LineUpdateRequest request) {
        Line line = findByIdWithStations(request.getId());
        line.changeName(new LineName(request.getName()));
        line.changeColor(new LineColor(request.getColor()));
        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLine(Long lineId) {
        Line line = findByIdWithStations(lineId);
        line.delete();
        lineRepository.delete(line);
    }

    public LineResponse findLineWithStations(Long lineId) {
        Line line = findByIdWithStations(lineId);
        return LineResponse.of(line);
    }

    public Line findByIdWithStations(Long lineId) {
        return lineRepository.findByIdWithStations(lineId)
                .orElseThrow(NotFoundEntityException::new);
    }

    public List<LineResponse> findAllLinesWithStations() {
        return lineRepository.findAllWithStations().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }
}
