package nextstep.subway.line.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.line.exception.AlreadyInitializedException;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new LinkedList<>();

    protected Sections() {
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public void addSection(Section section) {
        validateSection(section);
        sections.add(section);
    }

    private void validateSection(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException("null 인 구간을 추가할 수는 없습니다.");
        }

        if (sections.contains(section)) {
            throw new AlreadyInitializedException("이미 본 노선에 존재하는 구간입니다.");
        }

        if (section.isAddedToLine()) {
            throw new AlreadyInitializedException("이미 특정 노선에 포함되어있는 구간입니다.");
        }
    }
}
