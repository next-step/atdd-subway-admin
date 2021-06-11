package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> getSectionsByLineId(Long lineId) {
        List<Section> sections = sectionRepository.findAllByLine_Id(lineId);
        return Sections.of(sections).getOrderedSections().stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }
}

