package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
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
                toStation(lineRequest.getUpStationId()),
                toStation(lineRequest.getDownStationId())));
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id).orElse(null);
        return LineResponse.of(line);
    }

    private Station toStation(Long stationId) {
        return stationService.findById(stationId);
    }

    public void updateLine(Long id, LineUpdateRequest request) {
        Line line = lineRepository.findById(id).orElse(null);
        line.updateNameColor(request.getName(), request.getColor());
    }
}
