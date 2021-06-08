package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;

    private SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLineWithSection());
        return LineResponse.of(persistLine);
    }

    public LineResponse updateLine(Long id, LineRequest request) {
        Line persistLine = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        persistLine.update(request.toLine());
        lineRepository.save(persistLine);
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public SectionResponse addSection(Long lineId, SectionRequest request) {
        Section section = request.toSection();
        Line persistLine = lineRepository.findById(lineId).orElseThrow(EntityNotFoundException::new);
        persistLine.addSection(section);
        return SectionResponse.of(sectionRepository.save(section));
    }

    public List<SectionResponse> findAllSections(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(EntityNotFoundException::new);
        return SectionResponse.toSectionResponses(line.getSections());
    }
}
