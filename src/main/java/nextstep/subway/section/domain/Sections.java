package nextstep.subway.section.domain;

import nextstep.subway.section.domain.spcification.SectionsAddableSpecifications;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections.addAll(sections);
    }

    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : sections) {
            stations.addAll(
                    section.getStations()
            );
        }

        return stations;
    }

    public void addAndResizeDistanceBy(Section section) {
        resizeNearSection(section);

        sections.add(section);
    }

    public boolean isAddable(Station upStation, Station downStation, Distance distance) {
        if (sections.isEmpty()) {
            return true;
        }

        return new SectionsAddableSpecifications(sections, upStation, downStation, distance)
                .isAddable();
    }

    public SortedSections getSortedSections() {
        return new SortedSections(this.sections);
    }

    private void resizeNearSection(Section section) {
        sections.stream()
                .filter(item -> item.isSameUpStation(section) || item.isSameDownStation(section))
                .findFirst()
                .ifPresent((near) -> near.resizeAndChangeNearStation(section));
    }

}
