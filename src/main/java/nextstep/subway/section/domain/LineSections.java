package nextstep.subway.section.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.SortedStations;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

@Embeddable
public class LineSections implements Serializable {

    private static final long serialVersionUID = -4483053178441994936L;

    private static final String MESSAGE_SECTION_HAS_CYCLE = "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.";
    private static final String MESSAGE_SECTION_IS_NOT_UPDATABLE = "신규 구간의 상행역과 하행역 모두가 기존 구간에 포함되어 있지 않습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections;

    public LineSections() {
        sections = new ArrayList<>();
    }

    public LineSections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public Stations toStations() {
        return new SortedStations(this);
    }

    public void verifyStationCycle(Station upStation, Station downStation) {

        if (sections.isEmpty()) {
            return;
        }

        if (sections.size() == 1 && sections.get(0).isDuplicateSection(upStation, downStation)) {
            throw new IllegalArgumentException(MESSAGE_SECTION_HAS_CYCLE);
        }

        Optional<Section> maybeUpStation = sections.stream()
                                                   .filter(section -> section.contains(upStation))
                                                   .findAny();

        Optional<Section> maybeDownStation = sections.stream()
                                                     .filter(section -> section.contains(downStation))
                                                     .findAny();

        if (maybeUpStation.isPresent() && maybeDownStation.isPresent()) {
            throw new IllegalArgumentException(MESSAGE_SECTION_HAS_CYCLE);
        }
    }

    public void verifyNotUpdatable(Station upStation, Station downStation) {

        Optional<Section> updatableSection =
            sections.stream()
                    .filter(section -> section.equalsUpStation(downStation)
                        || section.equalsUpStation(upStation)
                        || section.equalsDownStation(upStation)
                        || section.equalsDownStation(downStation))
                    .findAny();

        if (!updatableSection.isPresent()) {
            throw new IllegalArgumentException(MESSAGE_SECTION_IS_NOT_UPDATABLE);
        }
    }

    public void update(Station upStation, Station downStation, int distance) {

        if (addNewSection(upStation, downStation, distance)) {
            return;
        }

        addNewSectionAndUpdate(upStation, downStation, distance);
    }

    private boolean addNewSection(Station upStation, Station downStation, int newDistance) {

        Optional<Section> maybeSection =
            sections.stream()
                    .filter(section -> section.equalsUpStation(downStation)
                        || section.equalsDownStation(upStation))
                    .findAny();

        if (!maybeSection.isPresent()) {
            return false;
        }

        sections.add(new Section(upStation, downStation, new Distance(newDistance)));
        return true;
    }

    private void addNewSectionAndUpdate(Station upStation, Station downStation, int newDistance) {

        Optional<Section> maybeSection =
            sections.stream()
                    .filter(section -> section.equalsUpStation(upStation)
                        || section.equalsDownStation(downStation))
                    .findAny();

        if (!maybeSection.isPresent()) {
            return;
        }

        sections.add(new Section(upStation, downStation, newDistance));

        Section section = maybeSection.get();
        section.minusDistance(newDistance);

        if (section.equalsUpStation(upStation)) {
            section.updateUpStation(downStation);
            return;
        }

        section.updateDownStation(upStation);
    }
}
