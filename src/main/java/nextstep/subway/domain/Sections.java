package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> sectionList = new ArrayList<>();

    public List<Section> getSectionList() {
        return sectionList;
    }

    public void add(Section section) {
        if (sectionList.isEmpty()) {
            sectionList.add(section);
            return;
        }
        validContains(section);
        updateExitSection(section);
        addSection(section);
    }

    private void validContains(Section section) {
        boolean isUpStationContains = isContains(section.getUpStation());
        boolean isDownStationContains = isContains(section.getDownStation());

        if (isUpStationContains && isDownStationContains) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 등록되어 있으면 추가할 수 없습니다.");
        }

        if (!isUpStationContains && !isDownStationContains) {
            throw new IllegalArgumentException("상행역과 하행역 중 하나는 포함되어야 합니다.");
        }
    }

    private boolean isContains(Station station) {
        return this.sectionList.stream()
                .anyMatch(section -> section.isContains(station));
    }

    private void updateExitSection(Section newSection) {
        Optional<Section> optionalSection = this.sectionList.stream()
                .filter(section -> section.isContains(newSection.getUpStation()) || section.isContains(newSection.getDownStation()))
                .findFirst();

        Section exitSection = optionalSection.get();
        validDistance(newSection.getDistance(), exitSection.getDistance());

        this.sectionList = this.sectionList.stream()
                .filter(section -> section.equals(exitSection))
                .map(section -> {section.updateSection(newSection); return section;})
                .collect(Collectors.toList());
    }

    private void validDistance(Long newDistance, Long exitDistance) {
        if (exitDistance <= newDistance) {
            throw new IllegalArgumentException("신규 구간 입력 시 기존 구간보다 길이가 작아야합니다.");
        }
    }

    private void addSection(Section section) {
        sectionList.add(section);
    }
}
