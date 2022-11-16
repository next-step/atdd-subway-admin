package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Service
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional(isolation = READ_COMMITTED)
    public LineResponse create(Line line) {
        line.addStations(stationService.findStationByIdToLineResponse(
                Arrays.asList(line.getUpStationId(), line.getDownStationId())));
        final Line resultLine = lineRepository.save(line);
        return resultLine.toLineResponse();
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findList() {
        return lineRepository.findAll().stream()
                .map(Line::toLineResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse find(long id) {
        return lineRepository.findById(id)
                .map(Line::toLineResponse)
                .orElse(null);
    }

    @Transactional(isolation = READ_COMMITTED)
    public void update(long id, LineUpdateRequest request) {
        Optional<Line> line = lineRepository.findById(id);
        lineRepository.save(line.orElseThrow(IllegalArgumentException::new)
                .updateName(request.getName())
                .updateColor(request.getColor()));
    }
}
