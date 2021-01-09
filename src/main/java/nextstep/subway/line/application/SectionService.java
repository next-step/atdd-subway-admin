package nextstep.subway.line.application;

import nextstep.subway.line.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    public void addSection(Long lineId, SectionRequest sectionRequest) {
    }
}
