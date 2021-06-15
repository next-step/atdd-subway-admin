package nextstep.subway.section.application;

import nextstep.subway.exception.DataNotFoundException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public SectionResponse findSectionById(Long id) {
        return SectionResponse.of(findById(id));
    }

    public Section findById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("일치하는 구간을 찾을 수 없습니다."));
    }

    public SectionResponse findSectionByUpStationId(Long upStationId) {
        return SectionResponse.of(findByUpStationId(upStationId));
    }

    public Section findByUpStationId(Long id) {
        return sectionRepository.findByUpStationId(id)
                .orElseThrow(() -> new DataNotFoundException("일치하는 구간을 찾을 수 없습니다."));
    }
}
