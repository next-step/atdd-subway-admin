package nextstep.subway.line.domain;

import java.util.List;

public interface SectionState {
    void add(Section section, Section newSection);
    void remove(List<Section> sections);
}
