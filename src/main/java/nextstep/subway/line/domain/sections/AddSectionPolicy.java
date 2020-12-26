package nextstep.subway.line.domain.sections;

public interface AddSectionPolicy {
    boolean addSection(final Section newSection, final Sections sections);
}
