package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.dto.SectionResponses;
import nextstep.subway.exception.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SectionService {
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    public SectionService(LineRepository lineRepository,
                          SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional(readOnly = true)
    public SectionResponses findAllSections(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(LineNotFoundException::new);
        List<Section> sections = new ArrayList<>(sectionRepository.findByLine(line));
        return SectionResponses.of(sections);
    }
}
