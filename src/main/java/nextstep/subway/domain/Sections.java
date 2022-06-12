package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public void addSection(Section section) {
        validateSection(section);

        if (sections.isEmpty()) {
            addFirstSection(section);
            return;
        }
        if (section.isBeforeUpFinalSection(sections)) {
            addUpFinalSection(section);
            return;
        }
        if (section.isAfterDownFinalSection(sections)) {
            addDownFinalSection(section);
            return;
        }
        addMiddleSection(section);
    }

    private void validateSection(Section section) {
        if (isEmpty()) {
            return;
        }
        if (hasSection(section)) {
            throw new IllegalArgumentException("추가할 구간은 모두 이미 노선에 등록되어 있습니다.");
        }
        if (!hasCommonStation(section)) {
            throw new IllegalArgumentException("추가할 구간의 상행역과 하행역 중 하나 이상은 노선에 포함되어 있어야 합니다.");
        }
    }

    public boolean isEmpty() {
        return sections == null || sections.isEmpty();
    }

    private boolean hasSection(Section section) {
        return section.matchUpAndDownStation(sections);
    }

    private boolean hasCommonStation(Section section) {
        return section.matchUpOrDownStation(sections);
    }

    private void addFirstSection(Section section) {
        sections.add(section);
        section.updateLineUpFinalStation();
    }

    private void addUpFinalSection(Section section) {
        sections.add(0, section);
        section.addLineDistance();
        section.updateLineUpFinalStation();
    }

    private void addDownFinalSection(Section section) {
        sections.add(section);
        section.addLineDistance();
        section.updateLineDownFinalStation();
    }

    private void addMiddleSection(Section section) {
        int position = findPositionToAddSection(section);
        addMiddleSection(position, section);
    }

    private void addMiddleSection(int position, Section section) {
        Section oldSection = sections.get(position);

        if (section.isLongerThan(oldSection)) {
            throw new IllegalArgumentException("새로운 구간의 길이는 기존 역 사이 길이보다 작아야 합니다.");
        }
        oldSection.updateUpStation(section);
        sections.add(position, section);
    }

    private int findPositionToAddSection(Section section) {
        Section oldSection = sections.stream()
                .filter(old -> old.hasSameUpStation(section))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("새로운 구간을 추가할 위치를 찾을 수 없습니다."));

        return sections.indexOf(oldSection);
    }
}
