package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Section;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Section section = sectionService.getSectionOf(request);
        Line persistLine = lineRepository.save(request.toLine(section));
        sectionService.saveSection(section);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> persistLines = lineRepository.findAll();
        return persistLines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        Line persistLine = fineById(id);
        return LineResponse.of(persistLine);
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest request) {
        Line persistLine = fineById(id);
        persistLine.update(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional
    public void deleteLineById(Long id) {
        Line persistLine = fineById(id);
        sectionService.delete(persistLine.getSections());
        lineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Line fineById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 id의 정류장이 존재하지 않습니다. id = " + id));
    }
}
