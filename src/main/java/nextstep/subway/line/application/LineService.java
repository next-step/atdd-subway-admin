package nextstep.subway.line.application;

import nextstep.subway.exception.LineNameAlreadyExistsException;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Line;
import nextstep.subway.section.domain.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(final LineRepository lineRepository, final SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public LineResponse saveLine(final LineRequest request) {
        verifySaveLine(request);

        final Line persistLine = lineRepository.save(request.toLine());
        sectionService.saveSection(request.toSectionRequest(), persistLine);

        return LineResponse.of(persistLine);
    }

    private void verifySaveLine(LineRequest request) {
        if (isExistsLineName(request)) {
            throw new LineNameAlreadyExistsException();
        }
    }

    private boolean isExistsLineName(LineRequest request) {
        return lineRepository.existsByName(request.getName());
    }

    @Transactional(readOnly = true)
    public LineResponses findAllLines() {
        return LineResponses.of(lineRepository.findAll());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(final long id) {
        return LineResponse.of(getLine(id));
    }

    public LineResponse updateLine(final long id, final LineRequest request) {
        final Line line = getLine(id);

        line.update(request.toLine());

        return LineResponse.of(line);
    }

    private Line getLine(final long id) {
        return lineRepository.findById(id).orElseThrow(IllegalStateException::new);
    }

    public void deleteLineById(final long id) {
        lineRepository.deleteById(id);
    }
}
