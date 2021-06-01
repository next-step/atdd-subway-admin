package nextstep.subway.line.application;

import nextstep.subway.common.exception.EntityNotExistException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public Long saveLine(Line line, long upStationId, long downStationId) {
        Line saved = lineRepository.save(line);
        sectionService.save(saved, upStationId, downStationId);

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
