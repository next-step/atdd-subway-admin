package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.DuplicateLineNameException;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.application.SectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {
    private final SectionService sectionService;
    private final LineRepository lineRepository;

    public LineService(SectionService sectionService, LineRepository lineRepository) {
        this.sectionService = sectionService;
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        lineRepository.findByName(request.getName())
                .ifPresent(line -> { throw new DuplicateLineNameException(line); });
        Line line = lineRepository.save(request.toLine());
        if (request.hasSectionArguments()) {
            sectionService.create(line, request.toSectionRequest());
        }
        return LineResponse.of(line);
    }

    public List<LineResponse> getLines() {
        return LineResponse.listOf(lineRepository.findAll());
    }

    public LineResponse getLine(Long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(() -> new LineNotFoundException(id));
    }

    public LineResponse modifyLine(Long id, LineRequest lineRequest) {
        return lineRepository.findById(id)
                .map(line -> line.update(lineRequest.toLine()))
                .map(LineResponse::of)
                .orElseThrow(() -> new LineNotFoundException(id));
    }

    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(id));
        lineRepository.delete(line);
    }
}
