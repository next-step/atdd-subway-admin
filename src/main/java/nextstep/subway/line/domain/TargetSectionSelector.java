package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;

public class TargetSectionSelector {
    private final List<Section> sections;

    public TargetSectionSelector(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    Section findTargetSection(final Section newSection) {
        Section targetSection = findSameWithUpStation(newSection);
        if (targetSection == null) {
            targetSection = findSameWithDownStation(newSection);
        }

        return targetSection;
    }

    private Section findSameWithUpStation(final Section section) {
        return this.sections.stream()
                .filter(it -> it.isSameUpStation(section) && !it.isSameDownStation(section))
                .findFirst()
                .orElse(null);
    }

    private Section findSameWithDownStation(final Section section) {
        return this.sections.stream()
                .filter(it -> !it.isSameUpStation(section) && it.isSameDownStation(section))
                .findFirst()
                .orElse(null);
    }
}
