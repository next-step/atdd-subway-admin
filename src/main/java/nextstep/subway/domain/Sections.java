package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
        sectionList.add(section);
    }

    private void validContains(Section section) {
        boolean isUpStationContains = isContains(section.getUpStation());
        boolean isDownStationContains = isContains(section.getDownStation());

        if (isUpStationContains && isDownStationContains) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 등록되어 있으면 추가할 수 없습니다.");
        }
    }

    private boolean isContains(Station station) {
        return this.sectionList.stream()
                .anyMatch(section -> section.isContains(station));
    }
}
