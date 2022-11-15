package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse create(Line line) {
        line.addStations(stationService.findStationByIdToLineResponse(
                Arrays.asList(line.getUpStationId(), line.getDownStationId())));
        final Line resultLine = lineRepository.save(line);
        return resultLine.toLineResponse();
    }

    public List<LineResponse> findList() {
        return lineRepository.findAll().stream()
                .map(Line::toLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse find(long id) {
        return lineRepository.findById(id)
                .map(Line::toLineResponse)
                .orElse(null);
    }

    public void update(long id, LineUpdateRequest request) {
        Optional<Line> line = lineRepository.findById(id);
        lineRepository.save(line.orElseThrow(IllegalArgumentException::new)
                .updateName(request.getName())
                .updateColor(request.getColor()));
    }
}
