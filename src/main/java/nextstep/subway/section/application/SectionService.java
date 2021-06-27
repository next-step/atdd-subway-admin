package nextstep.subway.section.application;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionResponse;

@Service
@Transactional
public class SectionService {

    private SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Transactional(readOnly = true)
    public SectionResponse findSectionById(Long id) {
        return SectionResponse.of(findSectionBySectionId(id));
    }

    protected Section findSectionBySectionId(Long id) {
        return sectionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

}
