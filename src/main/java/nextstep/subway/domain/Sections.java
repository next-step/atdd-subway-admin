package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
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

        if (isLongerThanStation(section)) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 추가할 수 없음");
        }
    }

    private boolean isContainAnyStation(Section section) {
        return sections.stream()
                .anyMatch(x -> x.getLineStations().contains(section.getUpStation())
                        || x.getLineStations().contains(section.getDownStation()));
    }

    private boolean isContainAllStation(Section section) {
        return sections.stream()
                .anyMatch(x -> x.getLineStations().contains(section.getUpStation())
                        && x.getLineStations().contains(section.getDownStation()));
    }

    private boolean isLongerThanStation(Section section) {
        return sections.stream()
                .filter(x -> x.getUpStation().equals(section.getUpStation())
                        || x.getDownStation().equals(section.getDownStation()))
                .anyMatch(x -> x.getDistance() <= section.getDistance());
    }

    private void updateSection(Section section) {
        Section targetSection = findTargetSection(section);
        targetSection.changeStationInfo(section);
    }

    private Section findTargetSection(Section section) {
        return sections.stream()
                .filter(x -> x.getLineStations().contains(section.getUpStation())
                        || x.getLineStations().contains(section.getDownStation()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("조건에 부합하는 데이터가 없습니다."));
    }
}
