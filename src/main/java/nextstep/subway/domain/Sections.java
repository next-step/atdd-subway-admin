package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.exception.AllRegisteredStationsException;
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
        Optional<Section> preLineStation = findByPreStationId(null);

        List<Section> list = new ArrayList<>();
        while (preLineStation.isPresent()) {
            Section section = preLineStation.get();
            list.add(section);
            preLineStation = findByPreStationId(section.getDownStation().getId());
        }
        return list;
    }

    public void add(Section newSection) {
        validate(newSection.getUpStationId(), newSection.getDownStation().getId());
        findStation(newSection.getDownStation().getId())
            .ifPresent(section -> section.updateFirstNode(newSection.getUpStationId()));
        findByPreStationId(newSection.getUpStationId())
            .ifPresent(section -> {
                section.updatePreStationAndDistance(newSection.getDownStation().getId(),
                    newSection.getDistance());
            });
        sections.add(newSection);
    }

    private void validate(Long upStationId, Long downStationId) {
        boolean isPresentUpStation = findStation(upStationId).isPresent();
        boolean isPresentDownStation = findStation(downStationId).isPresent();

        if (isPresentUpStation && isPresentDownStation) {
            throw new AllRegisteredStationsException();
        }

        if (!isPresentDownStation && !isPresentUpStation) {
            throw new NotAllIncludedStationsException();
        }
    }

    private Optional<Section> findByPreStationId(Long id) {
        return sections.stream()
            .filter(section -> section.getUpStationId() == id)
            .findFirst();
    }

    private Optional<Section> findStation(Long id) {
        return sections.stream()
            .filter(section -> section.getDownStation().getId() == id)
            .findFirst();
    }

}
