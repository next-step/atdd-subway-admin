package nextstep.subway.application;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.repository.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.ErrorMessage.NOT_FOUND;

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

        Line line = lineRepository.save(request.toLine());
        line.addSection(new Section(upStation, downStation, new Distance(request.getDistance())));
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public Line findById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND.getMessage()));
    }

    @Transactional
    public void updateLine(Long lineId, LineUpdateRequest request) {
        Line line = this.findById(lineId);
        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteById(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = this.findById(lineId);
        Station station = stationService.findById(stationId);

        line.removeSection(station);
    }
}
