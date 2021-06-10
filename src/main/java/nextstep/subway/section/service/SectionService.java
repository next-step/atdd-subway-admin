package nextstep.subway.section.service;

import nextstep.subway.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.section.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public SectionResponse createSection(SectionRequest sectionRequest) {
        Section section = Section.create(sectionRequest.getDistance());
        Section saveSection = sectionRepository.save(section);
        return SectionResponse.of(saveSection);
    }

}
