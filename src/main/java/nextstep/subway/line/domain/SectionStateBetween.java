package nextstep.subway.line.domain;

import static nextstep.subway.common.exception.ExceptionMessage.*;

import java.util.List;

public class SectionStateBetween implements SectionState {
    private final List<Section> sections;

    public SectionStateBetween(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    public void add(Section section, Section newSection) {
        if (newSection.getDistance() >= section.getDistance()) {
            throw new IllegalArgumentException(GREATER_THAN_OR_EQUAL_DISTANCE.getMessage());
        }
        newSection.changeLine(section.getLine());
        updateConnectStation(section, newSection);
        section.updateDistance(section.getDistance() - newSection.getDistance());
    }

    @Override
    public void remove(List<Section> sections) {
        Section section = sections.get(0);
        Section nextSection = sections.get(1);
        updateConnectRemoveStation(section, nextSection);
        section.updateDistance(section.getDistance() + nextSection.getDistance());
        this.sections.remove(nextSection);
    }

    private void updateConnectStation(Section section, Section newSection) {
        if (section.getUpStation().equals(newSection.getUpStation())) {
            section.updateUpStation(newSection.getDownStation());
        }
        if (section.getDownStation().equals(newSection.getDownStation())) {
            section.updateDownStation(newSection.getUpStation());
        }
    }

    private void updateConnectRemoveStation(Section section, Section nextSection) {
        if (section.getUpStation().equals(nextSection.getDownStation())) {
            section.updateUpStation(nextSection.getUpStation());
        }
        if (section.getDownStation().equals(nextSection.getUpStation())) {
            section.updateDownStation(nextSection.getDownStation());
        }
    }
}
