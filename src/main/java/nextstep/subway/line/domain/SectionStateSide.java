package nextstep.subway.line.domain;

import java.util.List;

public class SectionStateSide implements SectionState {
    private final List<Section> sections;

    public SectionStateSide(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    public void add(Section section, Section newSection) {
        newSection.changeLine(section.getLine());
    }

    @Override
    public void remove(List<Section> sections) {
        this.sections.remove(sections.get(0));
    }
}
