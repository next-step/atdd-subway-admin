package nextstep.subway.domain;

import nextstep.subway.exception.BothUpDownDoNotExistException;
import nextstep.subway.exception.SameSectionRegistrationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section newSection) {
        if (!sections.isEmpty()) {
            sectionValidate(newSection);
            sections.forEach(section -> section.separate(newSection));
        }

        sections.add(newSection);
    }

    private void sectionValidate(Section newSection) {
        sections.forEach(
                section -> {
                    List<Station> upDownStations = section.upDownStationPair();
                    if (upDownStations.contains(newSection.getUpStation()) && upDownStations.contains(newSection.getDownStation())) {
                        throw new SameSectionRegistrationException("상행역과 하행역이 이미 등록되어있습니다.");
                    }
                    if (!upDownStations.contains(newSection.getUpStation()) && !upDownStations.contains(newSection.getDownStation())) {
                        throw new BothUpDownDoNotExistException("상행역과 하행역 중 하나라도 등록되어 있지 않아 추가할 수 없습니다.");
                    }
                }
        );
    }

    public int totalDistanceLength() {
        return sections.stream()
                .mapToInt(Section::distanceValue)
                .sum();
    }

    public void remove(Station station) {
        sections.stream()
                .filter(section1 -> section1.getUpStation().equals(station))
                .findFirst()
                .ifPresent(section -> sections.remove(section));
    }
}
