package nextstep.subway.line.domain;

public class ChangeOriginalAndAddSectionPolicy implements AddSectionPolicy {
    private final Sections sections;

    public ChangeOriginalAndAddSectionPolicy(final Sections sections) {
        this.sections = sections;
    }

    @Override
    public boolean addSection(final Section newSection) {
        int originalSize = this.sections.size();

        Section targetSection = sections.findTargetSection(newSection);

        OriginalSectionCalculator originalSectionCalculator = OriginalSectionCalculator.find(targetSection, newSection);
        originalSectionCalculator.calculate(targetSection, newSection);
        sections.addSectionRaw(newSection);

        return (this.sections.size() == originalSize + 1);
    }
}
