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

        if (sections.stream()
                    .filter(section -> section.hasStation(upStation) || section.hasStation(downStation))
                    .count() >= 2) {
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

    public LineSections toNewSections(Station upStation, Station downStation, int distance) {

        List<Section> newSections = new ArrayList<>();

        for (Section section : sections) {

            if (section.equalsUpStation(downStation)) {
                newSections.add(new Section(upStation, downStation, distance));
                newSections.add(section.updateUpStation(downStation, distance));
                continue;
            }

            if (section.equalsUpStation(upStation)) {
                newSections.add(new Section(upStation, downStation, section.minusDistance(distance)));
                newSections.add(section.updateUpStation(downStation, distance));
                continue;
            }

            if (section.equalsDownStation(upStation)) {
                newSections.add(section.updateDownStation(upStation, distance));
                newSections.add(new Section(upStation, downStation, distance));
                continue;
            }

            if (section.equalsDownStation(downStation)) {
                newSections.add(section.updateDownStation(upStation, distance));
                newSections.add(new Section(upStation, downStation, section.minusDistance(distance)));
                continue;
            }

            if (section.hasNotUpAndDownStation(upStation, downStation)) {
                newSections.add(section);
            }
        }

        return new LineSections(newSections);
    }
}
