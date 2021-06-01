package nextstep.subway.line.application;

import nextstep.subway.common.exception.EntityNotExistException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.application.SectionCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineCommandService {
    private LineRepository lineRepository;
    private SectionCommandService sectionCommandService;

    public LineCommandService(LineRepository lineRepository, SectionCommandService sectionCommandService) {
        this.lineRepository = lineRepository;
        this.sectionCommandService = sectionCommandService;
    }

    public Long saveLine(Line line, long upStationId, long downStationId) {
        Line saved = lineRepository.save(line);
        sectionCommandService.save(saved, upStationId, downStationId);

        return saved.getId();
    }

    public Long update(Long id, Line updateLine) {
        Line line = findById(id);

        line.update(updateLine);

        return line.getId();
    }

    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }


    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(EntityNotExistException::new);
    }
}
