package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

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
        validateAddSection(section);

        if (sections.isEmpty()
                || section.isBeforeUpFinalSection()
                || section.isAfterDownFinalSection()) {
            sections.add(section);
            return;
        }
        addMiddleSection(section);
    }

    private void validateAddSection(Section section) {
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

    public List<Section> getSectionsInOrder() {
        List<Section> allSections = new ArrayList<>();

        Station nextStation = getUpFinalStation();
        Station downFinalStation = getDownFinalStation();
        while (!nextStation.equals(downFinalStation)) {
            Optional<Section> nextSection = getSectionByUpStation(nextStation);
            if (!nextSection.isPresent()) {
                break;
            }

            allSections.add(nextSection.get());
            nextStation = nextSection.get().getDownStation();
        }
        return allSections;
    }

    private Station getUpFinalStation() {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation).collect(Collectors.toList());
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation).collect(Collectors.toList());
        upStations.removeAll(downStations);

        if (upStations.size() != 1) {
            throw new RuntimeException("상행종점역을 찾을 수 없습니다.");
        }
        return upStations.get(0);
    }

    private Station getDownFinalStation() {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation).collect(Collectors.toList());
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation).collect(Collectors.toList());
        downStations.removeAll(upStations);

        if (downStations.size() != 1) {
            throw new RuntimeException("하행종점역을 찾을 수 없습니다.");
        }
        return downStations.get(0);
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

    private void validateRemoveSection(Station station) {
        if (sections.size() <= 1) {
            throw new RuntimeException("현재 지하철 구간이 하나인 경우 삭제할 수 없습니다.");
        }
        if (!hasSectionWithStation(station)) {
            throw new IllegalArgumentException("존재하지 않는 구간은 삭제할 수 없습니다.");
        }
    }

    private boolean hasSectionWithStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.containsStation(station));
    }

