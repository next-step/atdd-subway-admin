package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRepository.save(Line.of(lineRequest.getName(),
                lineRequest.getColor(),
                lineRequest.getDistance(),
                stationService.findById(lineRequest.getUpStationId()),
                stationService.findById(lineRequest.getDownStationId())));
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id).orElse(null);
        return LineResponse.of(line);
    }

    public void updateLine(Long id, LineUpdateRequest request) {
        Line line = lineRepository.findById(id).orElse(null);
        line.updateNameColor(request.getName(), request.getColor());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
