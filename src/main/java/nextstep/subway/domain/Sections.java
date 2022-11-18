package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
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

        Optional<Section> matchDownSection = findMatchSectionByDownStation(downStation);
        Optional<Section> matchUpSection = findMatchSectionByUpStation(upStation);

        validStation(matchDownSection, matchUpSection);

        insertSectionIntoFrontSide(upStation, distance, line, matchDownSection);
        insertSectionIntoBackSide(downStation, distance, line, matchUpSection);

    }

    public void deleteSection(Station station) {
        Optional<Section> matchDownSection = findMatchSectionByDownStation(station);
        Optional<Section> matchUpSection = findMatchSectionByUpStation(station);

        if (!matchDownSection.isPresent() && !matchUpSection.isPresent()) {
            throw new IllegalArgumentException("등록되지 않은 역입니다.");
        }

        if (sections.size() == 3) {
            throw new IllegalArgumentException("구간이 하나인 상태에서는 삭제할 수 없습니다.");
        }
    }

    private void insertSectionIntoBackSide(Station downStation, Integer distance, Line line, Optional<Section> matchUpSection) {
        if (matchUpSection.isPresent()) {
            Section section = matchUpSection.get();
            Section newSection = section.changeDownStation(downStation, distance);
            addSection(newSection, line);
        }
    }

    private void insertSectionIntoFrontSide(Station upStation, Integer distance, Line line, Optional<Section> matchDownSection) {
        if (matchDownSection.isPresent()) {
            Section section = matchDownSection.get();
            Section newSection = section.changeUpStation(upStation, distance);
            addSection(newSection, line);
        }
    }

    private static void validStation(Optional<Section> matchDownSection, Optional<Section> matchUpSection) {
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
