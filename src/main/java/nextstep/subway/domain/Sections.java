package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    private static final int MINIMUM_SECTIONS_SIZE = 3;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();


    public List<Station> getStationsInOrder() {
        List<Station> stations = new ArrayList<>();
        Optional<Station> station = findDownStationByUpStation(null);

        while (station.isPresent()) {
            Station nowStation = station.get();
            stations.add(nowStation);
            station = findDownStationByUpStation(nowStation);
        }
        return stations;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section, Line line) {
        if (!this.sections.contains(section)) {
            this.sections.add(section);
        }

        if (section.getLine() != line) {
            section.setLine(line);
        }
    }

    public void addSection(Station upStation, Station downStation, Integer distance, Line line) {
        if (sections.isEmpty()) {
            addInitialSections(upStation, downStation, distance, line);
            return;
        }

        Optional<Section> matchedSectionByDownStation = findMatchSectionByDownStation(downStation);
        Optional<Section> matchedSectionByUpStation = findMatchSectionByUpStation(upStation);

        validForAddSection(matchedSectionByDownStation, matchedSectionByUpStation);

        insertSectionIntoFrontSide(upStation, distance, line, matchedSectionByDownStation);
        insertSectionIntoBackSide(downStation, distance, line, matchedSectionByUpStation);

    }

    public void deleteSection(Station station) {
        Section matchedSectionByDownStation = findMatchSectionByDownStation(station)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 역입니다."));
        Section matchedSectionByUpStation = findMatchSectionByUpStation(station)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 역입니다."));

        validForDeleteSection();

        matchedSectionByUpStation.changeUpStationForDelete(matchedSectionByDownStation);
        sections.remove(matchedSectionByDownStation);
    }

    private void validForDeleteSection() {
        if (sections.size() == MINIMUM_SECTIONS_SIZE) {
            throw new IllegalArgumentException("구간이 하나인 상태에서는 삭제할 수 없습니다.");
        }
    }

    private void insertSectionIntoBackSide(Station downStation, Integer distance, Line line, Optional<Section> matchUpSection) {
        if (matchUpSection.isPresent()) {
            Section section = matchUpSection.get();
            Section newSection = section.changeDownStationForAdd(downStation, distance);
            addSection(newSection, line);
        }
    }

    private void insertSectionIntoFrontSide(Station upStation, Integer distance, Line line, Optional<Section> matchDownSection) {
        if (matchDownSection.isPresent()) {
            Section section = matchDownSection.get();
            Section newSection = section.changeUpStationForAdd(upStation, distance);
            addSection(newSection, line);
        }
    }

    private static void validForAddSection(Optional<Section> matchDownSection, Optional<Section> matchUpSection) {
        if (matchDownSection.isPresent() && matchUpSection.isPresent()) {
            throw new IllegalArgumentException("이미 등록되어 있는 역 입니다.");
        }

        if (!matchDownSection.isPresent() && !matchUpSection.isPresent()) {
            throw new IllegalArgumentException("추가할 수 없는 역 입니다.");
        }
    }

    private Optional<Section> findMatchSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(it -> it.getUpStation() == upStation)
                .findFirst();
    }

    private Optional<Section> findMatchSectionByDownStation(Station downStation) {
        return sections.stream()
                .filter(it -> it.getDownStation() == downStation)
                .findFirst();
    }

    private void addInitialSections(Station upStation, Station downStation, Integer distance, Line line) {
        List<Section> initialSections = Section.makeInitialSections(upStation, downStation, distance);
        initialSections.forEach(section -> addSection(section, line));
    }


    private Optional<Station> findDownStationByUpStation(Station station) {
        return sections.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst()
                .map(Section::getDownStation);
    }
}
