package nextstep.subway.line.domain;

public class ChangeOriginalAndAddSectionPolicy implements AddSectionPolicy {
    private final Sections sections;

    public ChangeOriginalAndAddSectionPolicy(final Sections sections) {
        this.sections = sections;
    }

    @Override
    public boolean addSection(final Section newSection) {
        return false;
    }
}
