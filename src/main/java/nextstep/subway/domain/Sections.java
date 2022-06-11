package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.springframework.dao.DataIntegrityViolationException;

@Embeddable
public class Sections {
    public static final String ERROR_EXISTS_SECTION = "이미 존재하는 구간입니다.";
    public static final String ERROR_CAN_NOT_CONNECT_SECTION = "연결되는 구간을 찾을 수 없습니다.";
    public static final String ERROR_NOT_FOUND_FIRST_STATION = "첫번째 역을 찾을 수 없습니다.";

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Station> getSortedStations() {
        List<Station> stations = new ArrayList<>();

        Station firstStation = getFirstStation();
        stations.add(firstStation);

        Station nextStation = getNextStation(firstStation);
        while(nextStation != null) {
            stations.add(nextStation);
            nextStation = getNextStation(nextStation);
        }

        return stations;
    }

    private Station getFirstStation() {
        return sections.stream().map(Section::getUpStation)
                .filter(upStation -> !isExistsDownStationOfSections(upStation)).findFirst()
                .orElseThrow(() -> new DataIntegrityViolationException(ERROR_NOT_FOUND_FIRST_STATION));
    }

    private boolean isExistsDownStationOfSections(Station station) {
        return sections.stream().anyMatch(section -> section.equalDownStation(station));
    }

    private Station getNextStation(Station currentStation) {
        return sections.stream().filter(section -> section.equalUpStation(currentStation))
                .map(Section::getDownStation).findFirst().orElse(null);
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateForAddSection(section);
        addSectionInternal(section);
    }

    private void addSectionInternal(Section section) {
        Section matchedSection = findMatchedSection(section);
        if (matchedSection != null) {
            addSectionWithUpdateMatchedSection(matchedSection, section);
            return;
        }

        sections.add(section);
    }

    private Section findMatchedSection(Section targetSection) {
        return sections.stream().filter(section -> section.match(targetSection)).findFirst().orElse(null);
    }


    private void addSectionWithUpdateMatchedSection(Section matchedSection, Section newSection) {
        matchedSection.updateForDivide(newSection);
        sections.add(newSection);
    }

    private void validateForAddSection(Section section) {
        validateExists(section);
        validateConnected(section);
    }

    private void validateExists(Section section) {
        if (checkExistsStation(section.getUpStation()) && checkExistsStation(section.getDownStation())) {
            throw new DataIntegrityViolationException(ERROR_EXISTS_SECTION);
        }
    }

    private void validateConnected(Section section) {
        if (!checkExistsStation(section.getUpStation()) && !checkExistsStation(section.getDownStation())) {
            throw new DataIntegrityViolationException(ERROR_CAN_NOT_CONNECT_SECTION);
        }
    }

    private boolean checkExistsStation(Station station) {
        return sections.stream().anyMatch(section -> section.hasStation(station));
    }
}
