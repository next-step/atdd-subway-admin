package nextstep.subway.line.domain;

public class SectionStateSide implements SectionState {
    @Override
    public void add(Section section, Section newSection) {
        newSection.changeLine(section.getLine());
    }
}
