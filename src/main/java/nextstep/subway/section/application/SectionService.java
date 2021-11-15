package nextstep.subway.section.application;

import org.springframework.stereotype.Service;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;

@Service
public class SectionService {
    private SectionRepository sectionRepository;

    public  SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }
}
