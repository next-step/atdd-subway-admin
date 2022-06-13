package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

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
        if (section.isBeforeUpFinalSection()) {
            addUpFinalSection(section);
            return;
        }
        if (section.isAfterDownFinalSection()) {
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
        sections.add(section);
        section.addLineDistance();
        section.updateLineUpFinalStation();
    }

    private void addDownFinalSection(Section section) {
        sections.add(section);
        section.addLineDistance();
        section.updateLineDownFinalStation();
    }

    private void addMiddleSection(Section section) {
        Section oldSection = findSectionToSplit(section);
        if (section.isLongerThan(oldSection)) {
            throw new IllegalArgumentException("새로운 구간의 길이는 기존 역 사이 길이보다 작아야 합니다.");
        }
        sections.add(section);
        oldSection.updateUpStation(section);
    }

    private Section findSectionToSplit(Section section) {
        return sections.stream()
                .filter(old -> old.hasSameUpStation(section))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("새로운 구간을 추가할 위치를 찾을 수 없습니다."));
    }

    public List<Section> getSectionsInOrder(Station startStation, Station endStation) {
        List<Section> allSections = new ArrayList<>();

        Station nextStation = startStation;
        while (!nextStation.equals(endStation)) {
            Optional<Section> nextSection = getSectionByUpStation(nextStation);
            if (!nextSection.isPresent()) {
                break;
            }

            allSections.add(nextSection.get());
            nextStation = nextSection.get().getDownStation();
        }
        return allSections;
    }

    private Optional<Section> getSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation) )
                .findFirst();
    }

    public int size() {
        return sections.size();
    }
}
