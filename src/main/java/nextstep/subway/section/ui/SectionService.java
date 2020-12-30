package nextstep.subway.section.ui;

import nextstep.subway.line.application.NoLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    private LineRepository lineRepository;

    public SectionService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> throwNoLineException(lineId));
        line.addSection(sectionRequest.toSection());
    }

    private NoLineException throwNoLineException(Long id) {
        return new NoLineException(id);
    }
}
