package nextstep.subway.line.domain;

import java.util.Optional;

public class LastSectionDeleteStrategy implements SectionDeleteStrategy {
    @Override
    public void delete(Line line, Optional<Section> sectionOfUpStation, Optional<Section> sectionOfDownStation) {
        line.deleteSection(sectionOfDownStation.get());
    }
}
