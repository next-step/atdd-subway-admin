package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        Section startSection = getStartSection();
        List<Station> stations = startSection.getStations();
        Section nextSection = getNextSection(startSection.getDownStation());
        while (nextSection != null) {
            stations.add(nextSection.getDownStation());
            nextSection = getNextSection(nextSection.getDownStation());
        }
        return stations;
    }

    private Section getStartSection() {
        Section ret = null;
        for (Section section : this.sections) {
            ret = isNotContainDownStations(section.getUpStation());
        }
        return ret;
    }

    private Section isNotContainDownStations(Station upStation) {
        return this.sections
                .stream()
                .filter(section -> isNotEqualsDownStation(section, upStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("순환 구간은 없습니다."));
    }

    private boolean isNotEqualsDownStation(Section section, Station upStation) {
        return !section.getDownStation().getId().equals(upStation.getId());
    }

    private Section getNextSection(Station downStation) {
        return this.sections
                .stream()
                .filter(section -> isNextStation(section, downStation))
                .findFirst()
                .orElse(null);
    }

    private boolean isNextStation(Section section, Station downStation) {
        return section.getUpStation().getId().equals(downStation.getId());
    }
}
