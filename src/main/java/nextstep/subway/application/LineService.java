package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        stationService.findStationById(lineRequest.getUpStationId()).ifPresent(line::addStation);
        stationService.findStationById(lineRequest.getDownStationId()).ifPresent(line::addStation);
        return LineResponse.of(lineRepository.save(line));
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public Optional<Line> findLineById(Long id) {
        return lineRepository.findById(id);
    }

    @Transactional
    public void update(Long id, LineUpdateRequest request) {
        lineRepository.findById(id).ifPresent(line -> line.modifyBy(request));
    }

    @Transactional
    public void deleteById(Long id) {
        lineRepository.findByIdWithStations(id).ifPresent(Line::resetStations);
        lineRepository.deleteById(id);
    }
}
