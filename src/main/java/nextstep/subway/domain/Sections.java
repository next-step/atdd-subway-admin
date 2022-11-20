package nextstep.subway.domain;

import nextstep.subway.exception.ErrorStatus;
import nextstep.subway.exception.IllegalRequestBody;
import nextstep.subway.exception.NotFoundSection;
import nextstep.subway.exception.NotFoundStation;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> values = new ArrayList<>();

    public void addSection(Line line, Station upStation, Station downStation, Long distance) {
        Section section = new Section(line, upStation, downStation, distance);
        validateDuplicate(section);
        addSection(section);
    }

    private void validateSectionExist(Section section) {
        boolean isExist = this.values.stream().noneMatch(v -> v.anyMatch(section));
        if (isExist) {
            throw new IllegalRequestBody(ErrorStatus.SECTION_STATION_ERROR.getMessage());
        }
    }

    private void addSection(Section section) {
        if (values.isEmpty()) {
            values.add(section);
            return;
        }
        validateSectionExist(section);
        add(section);

    }

    private void add(Section section) {
        if (isAddUpStationTerminus(section)) {
            this.values.add(section);
            return;
        }
        if (isAddDownStationTerminus(section)) {
            this.values.add(section);
            return;
        }
        addMiddle(section);
    }

    private boolean isAddDownStationTerminus(Section section) {
        return this.values.stream().anyMatch(v -> v.getDownStation().equals(section.getUpStation()));
    }

    private boolean isAddUpStationTerminus(Section section) {
        return this.values.stream().anyMatch(v -> v.getUpStation().equals(section.getDownStation()));
    }

    private void addMiddle(Section newSection) {
        if (isAddMiddleFromUpStation(newSection)) {
            updateUpStation(newSection);
            values.add(newSection);
            return;
        }
        if (isAddMiddleFromDownStation(newSection)) {
            updateDownStation(newSection);
            values.add(newSection);
        }
    }

    private void updateDownStation(Section newSection) {
        Section existSection = this.values.stream()
                .filter(v -> v.getDownStation().equals(newSection.getDownStation()))
                .findAny().get();
        existSection.updateDownStation(newSection);
    }

    private boolean isAddMiddleFromDownStation(Section newSection) {
        return this.values.stream()
                .anyMatch(v -> v.getDownStation().equals(newSection.getDownStation()));
    }

    private boolean isAddMiddleFromUpStation(Section newSection) {
        return this.values.stream()
                .anyMatch(v -> v.getUpStation().equals(newSection.getUpStation()));
    }

    private void updateUpStation(Section newSection) {
        Section existSection = this.values.stream()
                .filter(v -> v.getUpStation().equals(newSection.getUpStation()))
                .findAny().get();
        existSection.updateUpStation(newSection);
    }

    private void validateDuplicate(Section section) {
        values.forEach(v -> v.validateDuplicate(section));
    }

    public List<Station> allStation() {
        Station upStationTerminus = findUpStationTerminus();
        return Collections.unmodifiableList(new ArrayList<>(createStations(upStationTerminus)));
    }

    private Station findUpStationTerminus() {
        Set<Station> downStations = values.stream().map(Section::getDownStation).collect(Collectors.toSet());
        return values.stream().map(Section::getUpStation)
                .filter(v -> !downStations.contains(v)).findFirst().orElseThrow(NotFoundStation::new);
    }

    private List<Station> createStations(Station upStationTerminus) {
        Map<Station, Station> allStation = values.stream().collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        List<Station> stations = findStationRecursive(upStationTerminus, allStation);

        return Collections.unmodifiableList(new ArrayList<>(stations));
    }

    private List<Station> findStationRecursive(Station upStationTerminus, Map<Station, Station> allStation) {
        List<Station> stations = new ArrayList<>();
        stations.add(upStationTerminus);

        Station downStation = allStation.get(upStationTerminus);
        while (downStation != null) {
            stations.add(downStation);
            downStation = allStation.get(downStation);
        }

        return stations;
    }


    public List<Section> allSection() {
        Station upStationTerminus = findUpStationTerminus();
        return createSections(upStationTerminus);
    }

    private List<Section> createSections(Station upStationTerminus) {
        Section firstSection = values.stream().filter(v -> v.getUpStation().equals(upStationTerminus))
                .findFirst().orElseThrow(() -> new NotFoundSection(upStationTerminus.getId()));

        Map<Station, Section> allSection = values.stream().collect(Collectors.toMap(Section::getUpStation, Function.identity()));

        List<Section> sections = createSectionRecursive(firstSection, allSection);
        return Collections.unmodifiableList(new ArrayList<>(sections));
    }

    private List<Section> createSectionRecursive(Section firstSection, Map<Station, Section> allSection) {
        List<Section> sections = new ArrayList<>();
        sections.add(firstSection);

        Section section = allSection.get(firstSection.getDownStation());
        while (section != null) {
            sections.add(section);
            section = allSection.get(section.getDownStation());
        }

        return sections;
    }
}
