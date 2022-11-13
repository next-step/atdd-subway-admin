package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
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
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        Line saveLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(saveLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        line.updateNameAndColor(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }
}
