package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections.stream()
                .sorted((s1, s2) -> s1.getUpStation().getName().compareTo(s2.getDownStation().getName()))
                .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            validSectionCheck(section);
            updateSection(section);
        }
        sections.add(section);
    }

    private void validSectionCheck(Section section) {
        if (!isContainAnyStation(section)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음");
        }

        if (isContainAllStation(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있으면 추가할 수 없음");
        }
    }

    private boolean isContainAnyStation(Section newSection) {
        return sections.stream()
                .anyMatch(currentSection -> currentSection.getLineStations().contains(newSection.getUpStation())
                        || currentSection.getLineStations().contains(newSection.getDownStation()));
    }

    private boolean isContainAllStation(Section newSection) {
        return sections.stream()
                .anyMatch(currentSection -> currentSection.getLineStations().contains(newSection.getUpStation())
                        && currentSection.getLineStations().contains(newSection.getDownStation()));
    }

    private void updateSection(Section section) {
        Section targetSection = findTargetSection(section);
        targetSection.changeStationInfo(section);
    }

    private Section findTargetSection(Section newSection) {
        return sections.stream()
                .filter(currentSection -> currentSection.getLineStations().contains(newSection.getUpStation())
                        || currentSection.getLineStations().contains(newSection.getDownStation()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("조건에 부합하는 데이터가 없습니다."));
    }
}
