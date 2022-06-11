package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.StationNotFoundException;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    private static final int FIRST_INDEX = 0;
    private static final int ONLY_ONE_SIZE = 1;

    public void add(Section section) {
        sections.add(section);
    }


    public void update(Section requestSection) {
        Section hasUpStationSection = findSameUpStationSection(requestSection);
        Section hasDownStationSection = findSameDownStationSection(requestSection);

        validateUpdate(hasUpStationSection, hasDownStationSection);
        updateSection(hasUpStationSection, hasDownStationSection, requestSection);

        sections.add(requestSection);
    }

    public void delete(Station station) {
        validateDelete(station);
        if (firstStation().equals(station)) {
            deleteFirstStation(station);
            return;
        }
        if (lastStation().equals(station)) {
            deleteLastStation(station);
            return;
        }
        deleteMiddleStation(station);
    }

    private void deleteFirstStation(Station station) {
        Section firstSection = sections.stream()
            .filter(section -> section.getUpStation().equals(station)).findFirst().get();
        firstSection.setLine(null);
        sections.remove(firstSection);
    }

    private void deleteLastStation(Station station) {
        Section lastSection = sections.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findFirst().get();
        lastSection.setLine(null);
        sections.remove(lastSection);
    }

    private void deleteMiddleStation(Station station) {
        Section upSection = sections.stream()
            .filter(section -> section.hasDownStation(station)).findFirst().get();

        Section downSection = sections.stream()
            .filter(section -> section.hasUpStation(station)).findFirst().get();

        upSection.updateDownStation(downSection.getDownStation());
        upSection.addDistance(downSection);

        downSection.setLine(null);
        sections.remove(downSection);
    }

    private void validateDelete(Station station) {
        if (!sections.stream().filter(section -> section.existStation(station))
            .findFirst().isPresent()) {
            throw new StationNotFoundException("지하철역이 노선 내 존재하지 않습니다");
        }
        if (sections.size() == ONLY_ONE_SIZE) {
            throw new IllegalArgumentException("구간이 하나 뿐일 경우 삭제할 수 없습니다.");
        }
    }

    private void validateUpdate(Section hasUpStationSection, Section hasDownStationSection) {
        if (hasDownStationSection != null && hasUpStationSection != null) {
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
        }
        if (hasDownStationSection == null && hasUpStationSection == null) {
            throw new IllegalArgumentException("연결할 수 있는 역이 없습니다.");
        }
    }

    private void updateSection(Section hasUpStationSection, Section hasDownStationSection,
        Section requestSection) {
        if (hasUpStationSection != null) {
            hasUpStationSection.updateUpStation(requestSection);
        }
        if (hasDownStationSection != null) {
            hasDownStationSection.updateDownStation(requestSection);
        }
    }

    private Section findSameUpStationSection(Section section) {
        return sections.stream().filter(s -> s.existUpStation(section)).findFirst().orElse(null);
    }

    private Section findSameDownStationSection(Section section) {
        return sections.stream().filter(s -> s.existDownStation(section)).findFirst().orElse(null);
    }

    public List<Station> orderedStations() {
        Station station = firstStation();
        List<Station> orderedStations = new ArrayList<>();
        orderedStations.add(station);

        Section nextSection = findNextSection(station);
        do {
            orderedStations.add(nextSection.getDownStation());
            nextSection = findNextSection(nextSection.getDownStation());
        } while (nextSection != null);

        return orderedStations;
    }

    private Section findNextSection(Station station) {
        Optional<Section> nextSection = sections.stream()
            .filter(section -> section.getUpStation().equals(station)).findFirst();
        return nextSection.orElse(null);
    }

    private Station firstStation() {
        List<Station> upStations = new ArrayList<>();
        List<Station> downStations = new ArrayList<>();

        sections.forEach(section -> {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        });

        upStations.removeAll(downStations);
        return upStations.get(FIRST_INDEX);
    }

    private Station lastStation() {
        List<Station> upStations = new ArrayList<>();
        List<Station> downStations = new ArrayList<>();

        sections.forEach(section -> {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        });

        downStations.removeAll(upStations);
        return downStations.get(FIRST_INDEX);
    }

}
