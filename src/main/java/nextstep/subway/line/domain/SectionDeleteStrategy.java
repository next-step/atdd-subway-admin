package nextstep.subway.line.domain;

import java.util.Optional;

public interface SectionDeleteStrategy {
    void delete(Line line, Optional<Section> sectionOfUpStation, Optional<Section> sectionOfDownStation);
}
