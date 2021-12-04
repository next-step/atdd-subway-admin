package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.common.exception.SubwayErrorCode;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

@Embeddable
public class Sections {
    private static final int MIN_SIZE = 1;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public static Sections empty() {
        return new Sections();
    }

    public boolean addSection(Section section) {
        return this.sections.add(section);
    }

    public List<Station> getStations() {
        Stations stations = makeStationPath();

        return stations.getStations();
    }

    public void update(Section added) {
        Stations stations = makeStationPath();
        stations.validateSection(added);

        updateIfDownStationEquals(added);
        updateIfUpStationEquals(added);
        addSection(added);
    }

    private Stations makeStationPath() {
        return new Stations(
            sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation))
        );
    }

    public void deleteStation(Station station) {
        Optional<Section> optionalDownSection = findSectionByUpStation(station);
        Optional<Section> optionalUpSection = findSectionByDownStation(station);

        boolean upSectionExists = optionalUpSection.isPresent();
        boolean downSectionExists = optionalDownSection.isPresent();

        validateDeleteStation(upSectionExists, downSectionExists);

        removeSectionIfExists(optionalUpSection);
        removeSectionIfExists(optionalDownSection);

        if (upSectionExists && downSectionExists) {
            addCombinedSection(optionalUpSection.get(), optionalDownSection.get());
        }
    }

    private void removeSectionIfExists(Optional<Section> optionalSection) {
        if (optionalSection.isPresent()) {
            Section section = optionalSection.get();
            sections.remove(section);
        }
    }

    private void addCombinedSection(Section upSection, Section downSection) {
        Section combinedSection = Section.combine(upSection, downSection);
        addSection(combinedSection);
    }

    private void validateDeleteStation(boolean containsUpStation, boolean containsDownStation) {
        if (!containsUpStation && !containsDownStation) {
            throw new SubwayException(SubwayErrorCode.STATION_NOT_EXISTS);
        }

        if (sections.size() <= MIN_SIZE) {
            throw new SubwayException(SubwayErrorCode.CANNOT_DELETE_LAST_LINE);
        }
    }

    private void updateIfDownStationEquals(Section added) {
        Optional<Section> optionalDownSection = findSectionByDownStation(added.getDownStation());

        if (optionalDownSection.isPresent()) {
            Section origin = optionalDownSection.get();
            origin.updateDown(added);
        }
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return this.sections
            .stream()
            .filter(section -> section.hasDownStation(downStation))
            .findFirst();
    }

    private void updateIfUpStationEquals(Section added) {
        Optional<Section> optionalUpSection = findSectionByUpStation(added.getUpStation());

        if (optionalUpSection.isPresent()) {
            Section origin = optionalUpSection.get();
            origin.updateUp(added);
        }
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return this.sections
            .stream()
            .filter(section -> section.hasUpStation(upStation))
            .findFirst();
    }
}
