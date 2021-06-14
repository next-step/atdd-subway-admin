package nextstep.subway.line.application;

import nextstep.subway.line.dto.SectionRequest;

public interface SectionCommandUseCase {
    void addSection(Long lineId, SectionRequest sectionRequest);
}
