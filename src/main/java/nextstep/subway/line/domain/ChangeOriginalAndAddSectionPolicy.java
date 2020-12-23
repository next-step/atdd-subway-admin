package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.TargetSectionNotFoundException;

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

        OriginalSectionCalculator originalSectionCalculator = OriginalSectionCalculator.find(targetSection, newSection);
        originalSectionCalculator.calculate(targetSection, newSection);
        sections.addSectionRaw(newSection);

        return (this.sections.size() == originalSize + 1);
    }

    private void validateTarget(final Section targetSection) {
        if (targetSection == null) {
            throw new TargetSectionNotFoundException("기존 구간 중 연결할 수 있는 구간이 없습니다.");
        }
    }
}
