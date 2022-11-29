package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        sections.add(new Section(downStation, upStation.getId(), distance));
    }

    public void remove() {
        sections.clear();
    }

    public List<Section> getStationsInOrder() {
        Optional<Section> preLineStation = findByUpStation(null);

        List<Section> list = new ArrayList<>();
        while (preLineStation.isPresent()) {
            Section section = preLineStation.get();
            list.add(section);
            preLineStation = findByUpStation(section.getDownStation().getId());
        }
        return list;
    }

    public void add(Section newSection) {
        validate(newSection.getUpStationId(), newSection.getDownStation().getId());
        findByDownStation(newSection.getDownStation().getId())
            .ifPresent(section -> section.updateFirstNode(newSection.getUpStationId()));
        findByUpStation(newSection.getUpStationId())
            .ifPresent(section -> {
                section.updatePreStationAndDistance(newSection.getDownStation().getId(),
                    newSection.getDistance());
            });
        sections.add(newSection);
    }

    public void deleteSection(Long stationId) {
        if (sections.size() == 2) {
            throw new LastSectionException();
        }
        findByDownStation(stationId)
            .ifPresent(section -> {
                findByUpStation(stationId).ifPresent(
                    downSection -> downSection.updateUpStation(section.getUpStationId(), section.getDistance()));
                this.sections.remove(section);
            });
    }

    private void validate(Long upStationId, Long downStationId) {
        boolean isPresentUpStation = findByDownStation(upStationId).isPresent();
        boolean isPresentDownStation = findByDownStation(downStationId).isPresent();

        if (isPresentUpStation && isPresentDownStation) {
            throw new AllRegisteredStationsException();
        }

        if (!isPresentDownStation && !isPresentUpStation) {
            throw new NotAllIncludedStationsException();
        }
    }

    private Optional<Section> findByUpStation(Long id) {
        return sections.stream()
            .filter(section -> section.getUpStationId() == id)
            .findFirst();
    }

    private Optional<Section> findByDownStation(Long id) {
        return sections.stream()
            .filter(section -> section.getDownStation().getId() == id)
            .findFirst();
    }

}
