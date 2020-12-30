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
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        addUpToUp(section);
        addDownToDown(section);
        sections.add(section);
    }

    private void addUpToUp(Section section) {
        sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(new Section(oldSection.getLine(), section.getDownStation(), oldSection.getDownStation(), oldSection.getDistance().get() - section.getDistance().get()));
                    sections.remove(oldSection);
                });
    }

    private void addDownToDown(Section section) {
        sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getDownStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(new Section(oldSection.getLine(), oldSection.getUpStation(), section.getUpStation(), section.getDistance().get()));
                    sections.remove(oldSection);
                });
    }

    public List<Station> getStations() {
        Section startSection = findStartSection();
        List<Station> stations = startSection.getStations();
        Optional<Section> nextSection = findNextSection(startSection.getDownStation());
        while (nextSection.isPresent()) {
            stations.add(nextSection.get().getDownStation());
            nextSection = findNextSection(nextSection.get().getDownStation());
        }
        return stations;
    }

    private Section findStartSection() {
        return sections.stream()
                .filter(s -> !isContainsDownStation(s.getUpStation()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private boolean isContainsDownStation(Station station) {
        boolean flag = false;
        for (Section section : sections) {
            flag = section.getDownStation() == station;
        }
        return flag;
    }

    private Optional<Section> findNextSection(Station downStation) {
        return sections.stream()
                .filter(section -> section.getUpStation() == downStation)
                .findFirst();
    }
}
