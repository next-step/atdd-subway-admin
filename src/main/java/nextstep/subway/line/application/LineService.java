package nextstep.subway.line.application;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        validateDuplicate(lineRequest);
        Line line = lineRepository.save(lineRequest.toLine());
        addSection(line, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = findLineById(id);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Line line = findLineById(id);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Line line = findLineById(id);
        addSection(line, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("존재하지않는 노선 ID 입니다."));
    }

    private void validateDuplicate(LineRequest lineRequest) {
        if (lineRepository.existsByName(lineRequest.getName())) {
            throw new BadRequestException("이미 존재하는 노선 이름입니다.");
        }
    }

    private void addSection(Line line, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.findStationById(upStationId);
        Station downStation = stationService.findStationById(downStationId);
        line.addSection(upStation, downStation, distance);
    }

    public LineResponse removeSection(Long id, Long stationId) {
        Line line = findLineById(id);
        Station removeStation = stationService.findStationById(stationId);
        line.removeSection(removeStation);
        return LineResponse.of(line);
    }
}
