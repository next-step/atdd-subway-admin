package nextstep.subway.line.domain.sections;

public class SimpleAddSectionPolicy implements AddSectionPolicy {
    private final Sections sections;

    public SimpleAddSectionPolicy(final Sections sections) {
        this.sections = sections;
    }

    @Override
    public boolean addSection(final Section newSection) {
        int originalSize = this.sections.size();
        this.sections.addSection(newSection);

        return (this.sections.size() == originalSize + 1);
    }
}
