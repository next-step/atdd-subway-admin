package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {

        if(validateEqualSection(section)) {
            throw new IllegalArgumentException("동일한 구간을 추가할 수 없습니다.");
        }

        if(validateUpStationOrDownStationNotContains(section)) {
            throw new IllegalArgumentException("상행과 하행 모두 포함되지 않습니다.");
        }

        if(validateGreaterEqualDistance(section)) {
            throw new IllegalArgumentException("거리가 작아야합니다.");
        }

        if(this.sections.stream().anyMatch(s -> s.matchUpStationFromUpStation(section))) {
            Section findSection = this.sections.stream()
                    .filter(s -> s.matchUpStationFromUpStation(section))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("일치하는 상행역이 존재하지 않습니다."));

            findSection.changeUpStationToDownStation(section);
        }

        if(this.sections.stream().anyMatch(s -> s.matchDownStationFromDownStation(section))) {

            Section findSection = this.sections.stream()
                    .filter(s -> s.matchDownStationFromDownStation(section))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("일치하는 하행역이 존재하지 않습니다."));

            findSection.changeDownStationToUpStation(section);
        }

        this.sections.add(section);
    }

    private Section matchStation(Section section) {
        return this.sections.stream()
                .filter(s -> s.matchUpStation(section) || s.matchDownStation(section))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상행과 하행 모두 포함되지 않습니다."));
    }

    private boolean isGreaterEqualDistance(Section section, Section otherSection) {
        return otherSection.isGreaterOrEqualDistance(section);
    }

    private boolean isNotEmpty() {
        return !this.sections.isEmpty();
    }

    private boolean validateEqualSection(Section section) {
        return this.sections.contains(section);
    }

    private boolean validateUpStationOrDownStationNotContains(Section section) {
        return isNotEmpty() && this.sections.stream().noneMatch(s -> s.matchUpStation(section) || s.matchDownStation(section));
    }

    private boolean validateGreaterEqualDistance(Section section) {
        return isNotEmpty() && isGreaterEqualDistance(matchStation(section), section);
    }

    public List<Section> getSections() {
        return sections;
    }

}
