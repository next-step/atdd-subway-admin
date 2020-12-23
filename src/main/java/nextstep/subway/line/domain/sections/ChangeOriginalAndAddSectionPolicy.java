package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.TargetSectionNotFoundException;
import nextstep.subway.line.domain.exceptions.TooLongSectionException;

public class ChangeOriginalAndAddSectionPolicy implements AddSectionPolicy {
    private final Sections sections;

    public ChangeOriginalAndAddSectionPolicy(final Sections sections) {
        this.sections = sections;
    }

    @Override
    public boolean addSection(final Section newSection) {
        int originalSize = this.sections.size();

        Section targetSection = sections.findTargetSection(newSection);
        validateTarget(targetSection);
        validateDistance(newSection, targetSection);

        OriginalSectionCalculator originalSectionCalculator = OriginalSectionCalculator.find(targetSection, newSection);
        originalSectionCalculator.calculate(targetSection, newSection);
        sections.addSection(newSection);

        return (this.sections.size() == originalSize + 1);
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
