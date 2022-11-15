package nextstep.subway.service;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ErrorMessage.LINE_ID_NOT_FOUND;

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
    public LineResponse create(LineRequest request) {
        Line line = request.toLine();
        Line savedLine = lineRepository.save(line);
        return LineResponse.of(savedLine);
    }

    private Section getSection(LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        return new Section(upStation, downStation, request.getDistance());
    }

    public List<LineResponse> findAllLines() {
        List<Line> stations = lineRepository.findAll();

        return stations.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = findById(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest request) {
        Line line = findById(id);
        line.update(request.toLine());
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = findById(id);
        lineRepository.delete(line);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new DataIntegrityViolationException(LINE_ID_NOT_FOUND.getMessage()));
    }

}
