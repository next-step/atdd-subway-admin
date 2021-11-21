package nextstep.subway.section.application;

import nextstep.subway.section.domain.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName : nextstep.subway.section.domain
 * fileName : SectionService
 * author : haedoang
 * date : 2021/11/20
 * description :
 */
@Service
@Transactional
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

}
