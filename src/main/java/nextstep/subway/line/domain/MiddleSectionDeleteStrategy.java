package nextstep.subway.line.domain;

import java.util.Optional;

public class MiddleSectionDeleteStrategy implements SectionDeleteStrategy {
    @Override
    public void delete(Line line, Optional<Section> sectionOfUpStation, Optional<Section> sectionOfDownStation) {
        Section upSection = sectionOfUpStation.get();
        Section downSection = sectionOfDownStation.get();

        downSection.extendSection(upSection);
        line.deleteSection(upSection);
    }
}
