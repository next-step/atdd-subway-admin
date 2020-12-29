package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Section;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;
    private SectionService sectionService;

    public LineService(LineRepository lineRepository,
          SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
              .map(LineResponse::of)
              .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = getLine(id);
        return LineResponse.of(line);
    }

    public LineResponse saveLine(LineRequest request) {
        Optional<Line> maybeLine = findByName(request.toLine());
        if (maybeLine.isPresent()) {
            Line line = addSection(request, maybeLine.get());
            return LineResponse.of(line);
        }

        Line persistLine = lineRepository.save(request.toLine());
        addSection(request, persistLine);
        return LineResponse.of(persistLine);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        line.update(lineRequest.toLine());

        return LineResponse.of(line);
    }

    public void deleteLine(Long id) {
        getLine(id);
        lineRepository.deleteById(id);
    }

    private Line addSection(LineRequest request, Line line) {
        Section section = sectionService.save(line, request);
        return section.getLine();
    }

    private Line getLine(Long id) {
        Optional<Line> maybeLine = findById(id);
        return maybeLine.orElseThrow(()
              -> new EntityNotFoundException("[id=" + id + "] 노선정보가 존재하지 않습니다."));
    }

    private Optional<Line> findById(Long id) {
        return lineRepository.findById(id);
    }

    private Optional<Line> findByName(Line line) {
        return lineRepository.findByName(line.getName());
    }
}
