package nextstep.subway.section.application;

import nextstep.subway.common.exception.EntityNotExistException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionQueryService {
    private SectionRepository sectionRepository;

    public SectionQueryService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Section findByIdFetched(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(EntityNotExistException::new);

        return section;
    }
}
