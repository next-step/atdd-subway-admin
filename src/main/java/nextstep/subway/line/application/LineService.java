package nextstep.subway.line.application;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line saveLine = lineRepository.save(request.toLine());
        saveLine.createSection(sectionService.createSection(request.toSection()));
        return LineResponse.of(saveLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.ofList(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(final Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(final Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);
        lineRepository.delete(line);
    }
}
