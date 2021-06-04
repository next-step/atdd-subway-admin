package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionCommandService {

    private final SectionRepository sectionRepository;

    public SectionCommandService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Long saveSection(Section entity) {
        Section persist = sectionRepository.save(entity);
        return persist.getId();
    }
}
