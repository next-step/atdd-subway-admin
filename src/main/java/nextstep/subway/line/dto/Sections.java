package nextstep.subway.line.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * 구간에 있는 지하철 역을 정렬하여 가지고 옵니다.
     * @return
     */
    public List<Station> getStations() {
        return this.sort().getSortedStation();
    }

    /**
     * 노선의 구간을 상행 종점부터 하행 종점까지 순서대로 정렬합니다.
     */
    private Sections sort() {
        List<Section> sortedSections = new ArrayList<>();

        // set first upStation
        this.sections.stream()
                .filter(section -> section.isEqualsUpStationId(this.findFirstUpStationInSections()))
                .findAny().ifPresent(sortedSections::add);

        // add Sections
        for (int i = 0; i < this.sections.size() - 1; i++) {
            Long downStationId = sortedSections.get(i).getDownStationId();
            this.sections.stream()
                    .filter(section -> section.isEqualsUpStationId(downStationId))
                    .findAny().ifPresent(sortedSections::add);
        }

        this.sections = sortedSections;
        return this;
    }

    /**
     * 노선의 구간에서 상행 종점역의 ID를 찾습니다.
     * @return 상행 종점역의 ID
     */
    private Long findFirstUpStationInSections() {
        Set<Long> upStationIds = this.sections.stream().map(Section::getUpStationId)
                .collect(Collectors.toSet());
        Set<Long> downStationIds = this.sections.stream().map(Section::getDownStationId)
                .collect(Collectors.toSet());
        upStationIds.removeAll(downStationIds);
        return upStationIds.stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("cannot found first up-station."));
    }

    /**
     * 정렬된 구간을 기준으로 역을 정렬합니다.
     * @return
     */
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
