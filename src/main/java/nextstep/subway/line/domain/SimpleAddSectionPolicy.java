package nextstep.subway.line.domain;

public class SimpleAddSectionPolicy implements AddSectionPolicy {
    private final Sections sections;

    public SimpleAddSectionPolicy(final Sections sections) {
        this.sections = sections;
    }

    @Override
    public boolean addSection(final Section newSection) {
        int originalSize = this.sections.size();

        if (sections.isEndSectionAddCase(newSection)) {
            this.sections.addSectionRaw(newSection);
            return (this.sections.size() == originalSize + 1);
        }

        return false;
    }
}
