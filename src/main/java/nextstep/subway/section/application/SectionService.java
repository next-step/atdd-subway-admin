package nextstep.subway.section.application;

import javax.persistence.EntityNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

    private SectionRepository sectionRepository;
    private LineRepository lineRepository;

    public SectionService(SectionRepository sectionRepository, LineRepository lineRepository) {
        this.sectionRepository = sectionRepository;
        this.lineRepository = lineRepository;
    }

    public SectionResponse saveSection(SectionRequest request) {
        Line line = lineRepository.findById(request.getLineId()).orElseThrow(EntityNotFoundException::new);
        Section section = request.toSection();
        section.toLine(line);
        Section persistSection = sectionRepository.save(section);
        return SectionResponse.of(persistSection);
    }
}
