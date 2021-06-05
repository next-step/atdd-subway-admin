package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.station.application.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, StationService stationService, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        sectionService.saveFirstSection(persistLine, stationService.findById(request.getUpStationId()));
        sectionService.saveSection(persistLine, stationService.findById(request.getDownStationId()), request.getDistance());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = findById(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void editLine(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    private Line findById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    @Transactional
    public void deleteLineById(Long id) {
        Line line = findById(id);
        sectionService.deleteSections(line.getSections());
        lineRepository.delete(line);
    }
}
