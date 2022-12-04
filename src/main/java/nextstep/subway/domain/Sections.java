package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.exception.AllRegisteredStationsException;
import nextstep.subway.exception.LastSectionException;
import nextstep.subway.exception.NotAllIncludedStationsException;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public void init(Station upStation, Station downStation, Integer distance) {
        sections.add(new Section(upStation, null, null));
        sections.add(new Section(downStation, upStation, distance));
    }

    public List<Section> getStationsInOrder() {
        Optional<Section> findSection = findByUpStation(null);

        List<Section> list = new ArrayList<>();
        while (findSection.isPresent()) {
            Section section = findSection.get();
            list.add(section);
            findSection = findByUpStation(section.getDownStation().getId());
        }
        return list;
    }

    public void add(Section newSection) {
        List<Station> stations = allStations();
        boolean existsUpStation = stations.contains(newSection.getUpStation());
        boolean existsDownStation = stations.contains(newSection.getDownStation());
        validateStations(existsUpStation, existsDownStation);
        findByDownStation(newSection.getDownStation().getId())
            .ifPresent(
                section -> section.updateWhenDownStationExists(newSection.getUpStation(), newSection.getDistance()));
        findByUpStation(newSection.getUpStation().getId())
            .ifPresent(
                section -> section.updateWhenUpStationExists(newSection.getDownStation(), newSection.getDistance()));
        sections.add(newSection);
    }

    public void deleteSection(Long stationId) {
        validateLastSection();
        findByDownStation(stationId).ifPresent(updateUpStation(stationId));
    }

    public void remove() {
        sections.clear();
    }

    private Consumer<Section> updateUpStation(Long stationId) {
        return section -> {
            findByUpStation(stationId).ifPresent(
                downSection -> downSection.updateUpStationAndMergeDistance(section.getUpStation(),
                    section.getDistance()));
            this.sections.remove(section);
        };
    }

    private List<Station> allStations() {
        List<Station> stations = this.sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
        stations
            .add(findByUpStation(null).orElseThrow(RuntimeException::new).getUpStation());
        return stations;
    }

    private void validateLastSection() {
        if (sections.size() == 2) {
            throw new LastSectionException();
        }
    }

    private void validateStations(boolean existsUpStation, boolean existsDownStation) {
        if (existsUpStation && existsDownStation) {
            throw new AllRegisteredStationsException();
        }

        if (!existsUpStation && !existsDownStation) {
            throw new NotAllIncludedStationsException();
        }
    }

    private Optional<Section> findByUpStation(Long id) {
        return sections.stream()
            .filter(section -> {
                if (section.getUpStation() != null) {
                    return section.getUpStation().getId().equals(id);
                }
                return id == null;
            })
            .findFirst();
    }

    private Optional<Section> findByDownStation(Long id) {
        return sections.stream()
            .filter(section -> section.getDownStation().getId() == id)
            .findFirst();
    }

}
