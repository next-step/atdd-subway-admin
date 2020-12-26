package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.TargetSectionNotFoundException;
import nextstep.subway.line.domain.exceptions.TooLongSectionException;

public class ChangeOriginalAndAddSectionPolicy implements AddSectionPolicy {
    @Override
    public boolean addSection(final Section newSection, final Sections sections) {
        int originalSize = sections.size();

        Section targetSection = sections.findTargetSection(newSection);
        validate(newSection, targetSection, sections);

        OriginalSectionCalculator originalSectionCalculator = OriginalSectionCalculator.find(targetSection, newSection);
        originalSectionCalculator.calculate(targetSection, newSection);
        sections.addSection(newSection);

        return (sections.size() == originalSize + 1);
    }

    private void validate(final Section newSection, final Section targetSection, final Sections sections) {
        validateTarget(targetSection);
        validateAlreadyIn(newSection, sections);
        validateDistance(newSection, targetSection);
    }

    private void validateAlreadyIn(final Section newSection, final Sections sections) {
        if (sections.isAllStationsIn(newSection)) {
            throw new TargetSectionNotFoundException("이미 모든 역이 노선에 존재합니다.");
        }
    }

    private void validateTarget(final Section targetSection) {
        if (targetSection == null) {
            throw new TargetSectionNotFoundException("기존 구간 중 연결할 수 있는 구간이 없습니다.");
        }
    }

    private void validateDistance(final Section newSection, final Section targetSection) {
        if (newSection.isHasBiggerDistance(targetSection)) {
            throw new TooLongSectionException("추가할 구간의 길이가 너무 깁니다.");
        }
    }
}
