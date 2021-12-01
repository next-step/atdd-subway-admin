package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Section saveSection(SectionRequest sectionRequest) {
        Section section = sectionRepository.save(sectionRequest.toSection());
        return section;
    }

    public void addSection(Long id, SectionRequest sectionRequest) {
        List<Section> sectionList = sectionRepository.findByLineId(id);
    }
}
