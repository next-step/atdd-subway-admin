package nextstep.subway.section.application;

import nextstep.subway.common.exception.EntityNotExistException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.domain.Sections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SectionQueryService {
    private SectionRepository sectionRepository;

    public SectionQueryService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Section findByIdFetched(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(EntityNotExistException::new);
    }

    public Sections findAllByLineIdFetchedOrderByUpToDownStation(Long lineId) {
        List<Section> sections = sectionRepository.findAllByLineId(lineId);

        return new Sections(sections);
    }
}
