package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import org.springframework.stereotype.Service;

@Service
public class SectionQueryService {

    private final SectionRepository sectionRepository;

    public SectionQueryService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Section findById(Long sectionId) {
        return sectionRepository.findById(sectionId)
                                .orElseThrow(() -> new IllegalArgumentException("section을 찾을 수 없습니다."));
    }
}
