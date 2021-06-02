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

    public Long saveLine(Line line, Long upStationId, Long downStationId, Long distance) {
        Long lineId = lineRepository.save(line)
                .getId();
        sectionCommandService.save(lineId, upStationId, downStationId, distance);

        return lineId;
    }

    public Long update(Long id, Line updateLine) {
        Line line = findById(id);

        line.update(updateLine);

        return line.getId();
    }

    public void deleteById(Long id) {
        sectionCommandService.deleteByLineId(id);
        lineRepository.deleteById(id);
    }


    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(EntityNotExistException::new);
    }
}
