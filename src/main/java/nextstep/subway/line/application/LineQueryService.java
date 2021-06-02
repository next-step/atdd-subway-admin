package nextstep.subway.line.application;

import nextstep.subway.common.exception.EntityNotExistException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineQueryService {
    private LineRepository lineRepository;

    public LineQueryService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Line findByIdFetched(Long id) {
        Line line = findById(id);
        line.getStationInSections();

        return line;
    }

    public List<Line> findAllFetched() {
        List<Line> lines = lineRepository.findAll();

        lines.forEach(Line::getStationInSections);

        return lines;
    }

    private Line findById(long id) {
        return lineRepository.findById(id)
                .orElseThrow(EntityNotExistException::new);
    }
}
