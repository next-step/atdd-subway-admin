package nextstep.subway.line.domain.sections;

public class SimpleAddSectionPolicy implements AddSectionPolicy {
    @Override
    public boolean addSection(final Section newSection, final Sections sections) {
        int originalSize = sections.size();
        sections.addSection(newSection);

        return (sections.size() == originalSize + 1);
    }
}
