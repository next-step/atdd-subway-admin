package nextstep.subway.line.domain;

import static nextstep.subway.common.exception.ExceptionMessage.*;

public class SectionStateBetween implements SectionState {
    @Override
    public void add(Section section, Section newSection) {
        if (newSection.getDistance() >= section.getDistance()) {
            throw new IllegalArgumentException(GREATER_THAN_OR_EQUAL_DISTANCE.getMessage());
        }
        newSection.changeLine(section.getLine());
        updateConnectStation(section, newSection);
        section.updateDistance(section.getDistance() - newSection.getDistance());
    }

    private void updateConnectStation(Section section, Section newSection) {
        if (section.getUpStation().equals(newSection.getUpStation())) {
            section.updateUpStation(newSection.getDownStation());
        }
        if (section.getDownStation().equals(newSection.getDownStation())) {
            section.updateDownStation(newSection.getUpStation());
        }
    }
}
