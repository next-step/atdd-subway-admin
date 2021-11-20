package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
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
    private SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

}
