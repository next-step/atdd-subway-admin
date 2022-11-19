package nextstep.subway.line.domain;

import java.util.Optional;

public class FirstSectionDeleteStrategy implements SectionDeleteStrategy {
    @Override
    public void delete(Line line, Optional<Section> sectionOfUpStation, Optional<Section> sectionOfDownStation) {
        line.deleteSection(sectionOfUpStation.get());
    }
}
