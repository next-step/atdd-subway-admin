package nextstep.subway.section.application;

import nextstep.subway.section.domain.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    @Autowired
    private SectionRepository sectionRepository;
}
