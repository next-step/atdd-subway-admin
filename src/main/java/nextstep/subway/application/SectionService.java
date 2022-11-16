package nextstep.subway.application;

import nextstep.subway.dto.SectionListResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    public SectionResponse createSection(Long lineId, SectionRequest request) {
        return null;
    }

    public SectionListResponse querySections(Long lineId) {
        return null;
    }
}
