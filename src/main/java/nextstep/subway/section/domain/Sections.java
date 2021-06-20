package nextstep.subway.section.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @JsonBackReference
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        return this.sort().getSortedStation();
    }

    private Sections sort() {
        List<Section> sortedSections = new ArrayList<>();

        // set first upStation
        this.sections.stream()
            .filter(section -> section.getUpStation().getId()
                .equals(this.findFirstUpStationInSections()))
            .findAny().ifPresent(sortedSections::add);

        // add Sections
        for (int i = 0; i < this.sections.size() - 1; i++) {
            Long downStationId = sortedSections.get(i).getDownStation().getId();
            this.sections.stream()
                .filter(section -> section.getUpStation().getId().equals(downStationId))
                .findAny().ifPresent(sortedSections::add);
        }

        this.sections = sortedSections;
        return this;
    }

    private Long findFirstUpStationInSections() {
        Set<Long> upStationIds = this.sections.stream()
            .map(section -> section.getUpStation().getId())
            .collect(Collectors.toSet());
        Set<Long> downStationIds = this.sections.stream()
            .map(section -> section.getDownStation().getId())
            .collect(Collectors.toSet());
        upStationIds.removeAll(downStationIds);
        return upStationIds.stream().findFirst()
            .orElseThrow(() -> new IllegalArgumentException("cannot found first up-station."));
    }

    private List<Station> getSortedStation() {
        List<Station> sortedStations = new ArrayList<>();
        int i = 0;
        for (i = 0; i < this.sections.size(); i++) {
            sortedStations.add(this.sections.get(i).getUpStation());
        }
        sortedStations.add(this.sections.get(i - 1).getDownStation());
        return sortedStations;
    }
}