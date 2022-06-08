package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

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

    public boolean isEmpty() {
        return sections == null || sections.isEmpty();
    }

    public boolean containsSection(Section section) {
        Station newUpStation = section.getUpStation();
        Station newDownStation = section.getDownStation();

        return containsStation(newUpStation) && containsStation(newDownStation);
    }

    public boolean containsStation(Station station) {
        return sections.stream().anyMatch(section -> section.containsStation(station));
    }

    public void addFirstSection(Section section) {
        sections.add(section);
    }

    public void addUpFinalSection(Section section) {
        sections.add(0, section);
        section.addLineDistance();
    }

    public void addDownFinalSection(Section section) {
        sections.add(section);
        section.addLineDistance();
    }

    public void addMiddleSection(Section section) {
        int position = findPositionToAddSection(section);
        addMiddleSection(position, section);
    }
    
    private void addMiddleSection(int position, Section section) {
        Section oldSection = sections.get(position);

        if (oldSection.getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException("새로운 구간의 길이는 기존 역 사이 길이보다 작아야 합니다.");
        }

        oldSection.updateUpStation(section.getDownStation());
        oldSection.updateDistance(oldSection.getDistance() - section.getDistance());
        sections.add(position, section);
    }

    private int findPositionToAddSection(Section section) {
        Section oldSection = sections.stream()
                .filter(old -> old.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("새로운 구간을 추가할 위치를 찾을 수 없습니다."));

        return sections.indexOf(oldSection);
    }

    public List<Section> getSections() {
        return sections;
    }
}
