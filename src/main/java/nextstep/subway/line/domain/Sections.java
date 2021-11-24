package nextstep.subway.line.domain;

import nextstep.subway.exception.CannotAddSectionException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Embeddable
public class Sections {

    private static final String ERROR_MESSAGE_EQUAL_OR_MORE_DISTANCE = "기존 구간보다 길이가 길거나 같습니다.";
    private static final String ERROR_MESSAGE_DISCONNECTED_SECTION = "연결될 수 없는 구간입니다.";
    private static final String ERROR_MESSAGE_EXISTED_SECTION = "이미 존재하는 구간입니다.";

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_section"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public void addSection(Section requestSection) {
        if (sections.isEmpty()) {
            sections.add(requestSection);
            return;
        }

        addSectionInExistSections(requestSection);
    }

    public List<Station> getStations() {
        List<Station> result = new ArrayList<>();
        Set<Station> stationVisit = new HashSet<>();
        Map<Station, Station> upToDownStation = new HashMap<>();
        Map<Station, Station> downToUpStation = new HashMap<>();

        sections.forEach(section -> {
            upToDownStation.put(section.getUpStation(), section.getDownStation());
            downToUpStation.put(section.getDownStation(), section.getUpStation());
        });

        for (Map.Entry<Station, Station> entry : upToDownStation.entrySet()) {
            addSortedStation(result, stationVisit, upToDownStation, downToUpStation, entry);
        }

        return result;
    }

    private void addSortedStation(List<Station> result, Set<Station> stationVisit, Map<Station, Station> upToDownStation, Map<Station, Station> downToUpStation, Map.Entry<Station, Station> entry) {
        Station upStation = entry.getKey();

        if (stationVisit.contains(upStation)) {
            return;
        }

        upStation = findTopUpStation(downToUpStation, upStation);
        addAllSectionFromTopUpStation(result, stationVisit, upToDownStation, upStation);
    }

    private void addAllSectionFromTopUpStation(List<Station> result, Set<Station> stationVisit, Map<Station, Station> upToDownStation, Station upStation) {
        while (upToDownStation.containsKey(upStation)) {
            result.add(upStation);
            stationVisit.add(upStation);

            upStation = upToDownStation.get(upStation);
        }

        result.add(upStation);
    }

    private Station findTopUpStation(Map<Station, Station> downToUpStation, Station upStation) {
        while (downToUpStation.containsKey(upStation)) {
            upStation = downToUpStation.get(upStation);
        }
        return upStation;
    }

    private void addSectionInExistSections(Section requestSection) {
        Set<Station> existedStation = findAllStation();

        validateExistOrDisconnectSection(requestSection, existedStation);

        Optional<Station> existUpStation = findStation(requestSection.getUpStation(), existedStation);
        if (existUpStation.isPresent()) {
            addSectionByUpStation(requestSection);
            return;
        }

        Optional<Station> existDownStation = findStation(requestSection.getDownStation(), existedStation);
        if (existDownStation.isPresent()) {
            addSectionByDownStation(requestSection);
            return;
        }

        throw new IllegalArgumentException();
    }

    private void addSectionByUpStation(Section requestSection) {
        Map<Station, Section> upToDownSections = new HashMap<>();
        sections.forEach(section -> upToDownSections.put(section.getUpStation(), section));

        Section modifiedSection = upToDownSections.get(requestSection.getUpStation());

        if (modifiedSection == null) {
            sections.add(requestSection);
            return;
        }

        addSection(requestSection, modifiedSection, requestSection.getDownStation(), modifiedSection.getDownStation());
    }

    private void addSectionByDownStation(Section requestSection) {
        Map<Station, Section> downToUpSections = new HashMap<>();
        sections.forEach(section -> downToUpSections.put(section.getDownStation(), section));

        Section modifiedSection = downToUpSections.get(requestSection.getDownStation());

        if (modifiedSection == null) {
            sections.add(requestSection);
            return;
        }

        addSection(requestSection, modifiedSection, modifiedSection.getUpStation(), requestSection.getUpStation());
    }

    private void addSection(Section requestSection,
                            Section modifiedSection,
                            Station newUpStation,
                            Station newDownStation) {

        if (modifiedSection.isDistanceGraterThan(requestSection)) {
            throw new CannotAddSectionException(ERROR_MESSAGE_EQUAL_OR_MORE_DISTANCE);
        }

        sections.remove(modifiedSection);

        int totalDistance = modifiedSection.getDistance();
        int diffDistance = totalDistance - requestSection.getDistance();

        Section newSection = Section.of(newUpStation,
                newDownStation,
                diffDistance);

        sections.add(newSection);
        sections.add(requestSection);
    }

    private Set<Station> findAllStation() {
        Set<Station> existedStation = new HashSet<>();

        sections.forEach(section -> {
            existedStation.add(section.getUpStation());
            existedStation.add(section.getDownStation());
        });
        return existedStation;
    }

    private void validateExistOrDisconnectSection(Section requestSection, Set<Station> existedStation) {
        validateExistSection(requestSection, existedStation);
        validateDisconnectSection(requestSection, existedStation);
    }

    private void validateExistSection(Section requestSection, Set<Station> existedStation) {
        if (existedStation.contains(requestSection.getUpStation())
                && existedStation.contains(requestSection.getDownStation())) {
            throw new CannotAddSectionException(ERROR_MESSAGE_EXISTED_SECTION);
        }
    }

    private void validateDisconnectSection(Section requestSection, Set<Station> existedStation) {
        if (!existedStation.contains(requestSection.getUpStation())
                && !existedStation.contains(requestSection.getDownStation())) {
            throw new CannotAddSectionException(ERROR_MESSAGE_DISCONNECTED_SECTION);
        }
    }

    private Optional<Station> findStation(Station targetStation, Set<Station> existedStation) {
        return existedStation.stream()
                .filter(targetStation::equals)
                .findFirst();
    }
}
