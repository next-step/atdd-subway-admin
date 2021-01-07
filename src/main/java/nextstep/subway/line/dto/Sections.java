package nextstep.subway.line.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.Distance;
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
        if(!this.canAddSection(section)) {
            throw new IllegalArgumentException("추가 할 수 없는 구간입니다.");
        }

        if(this.canPutInMiddleSection(section)) {
            this.updatePutInSection(section);
        }
        this.sections.add(section);
    }

    /**
     * 구간 사이에 역을 추가 합니다.
     * @param newSection
     */
    private void updatePutInSection(Section newSection) {
        Section existingPutInSection = this.findPutInSection(newSection);

        if(this.equalsUpStation(newSection)) {
            existingPutInSection.setUpStation(newSection.getDownStation());
        }
        if(this.equalsDownStation(newSection)) {
            existingPutInSection.setDownStation(newSection.getUpStation());
        }

        existingPutInSection.setDistance(Distance.difference(existingPutInSection.getDistance(), newSection.getDistance()));
        this.sections.add(existingPutInSection);
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
        if(i != 0) {
            sortedStations.add(this.sections.get(i - 1).getDownStation());
        }
        return sortedStations;
    }

    /**
     * 정렬된 역 아이디를 가지고 옵니다
     * @return 
     */
    private List<Long> getSortedStationIds() {
        return this.getStations().stream().map(Station::getId).collect(Collectors.toList());
    }

    /**
     * 추가 할 수 있는 구간인지 여부 반환.
     * @param section 
     * @return
     */
    public boolean canAddSection(Section section) {
        return isFirstSection() ||
                this.isLastStopSection(section) || this.canPutInMiddleSection(section);
    }

    private boolean isFirstSection() {
        return this.getSections().size() == 0;
    }

    /**
     * 상행종점 혹은 하행 종점역을 가진 구간인지 반환합니다.
     * @param section
     * @return
     */
    public boolean isLastStopSection(Section section) {
        return this.isFirstSection(section) || this.isLastSection(section);
    }

    /**
     * 해당 구간의 상행역이 기존에 없는 역이고, 하행역은 기존의 상행종점인 경우.
     * @param section
     * @return
     */
    public boolean isFirstSection(Section section) {
        return this.notExistStation(section.getUpStationId()) && this.isFirstUpStation(section.getDownStationId());
    }

    /**
     * 구간 내에서 상행종점역인지 여부를 반환.
     * @param id
     * @return
     */
    private boolean isFirstUpStation(Long id) {
        return this.getSortedStationIds().stream().findFirst().equals(id);
    }

    /**
     * 해당 구간의 하행역이 기존에 없는 역이고, 상행역은 기존의 하행종점인 경우.
     * @param section
     * @return
     */
    public boolean isLastSection(Section section) {
        return this.notExistStation(section.getDownStationId()) && this.isLastDownStation(section.getUpStationId());
    }

    /**
     * 구간 내에서 하행종점역인지 여부를 반환.
     * @param id
     * @return
     */
    private boolean isLastDownStation(Long id) {
        List<Long> sortedStationIds = this.getSortedStationIds();
        if(sortedStationIds.size() < 1) {
            return false;
        }
        return sortedStationIds.get(sortedStationIds.size() - 1).equals(id);
    }

    /**
     * 구간에 존재하는 역인지 여부를 반환.
     * @param id
     * @return
     */
    private boolean notExistStation(Long id) {
        return this.getSortedStationIds().stream().noneMatch(stationId -> stationId.equals(id));
    }

    /**
     * 구간 사이에 구간을 추가 할 수 있는 지 여부.
     * @param section 
     * @return
     */
    private boolean canPutInMiddleSection(Section section) {
        return this.equalsUpStation(section) || this.equalsDownStation(section);
    }

    /**
     * 구간 사이에 구간을 추가 할 수 있는 경우에 상행역이 같은 경우.
     * @param section 
     * @return
     */
    private boolean equalsUpStation(Section section) {
        return this.existUpStations(section) && this.notExistStation(section.getDownStationId());
    }

    /**
     * 구간 사이에 구간을 추가 할 수 있는 경우에 하행역이 같은 경우.
     * @param section 
     * @return
     */
    private boolean equalsDownStation(Section section) {
        return this.existDownStations(section) && this.notExistStation(section.getDownStationId());
    }

    /**
     * 구간 사이에 구간을 추가 할 수 있는 경우에 구간을 추가 할 수 있는 구간을 찾아서 리턴.
     * @param section 
     * @return
     */
    private Section findPutInSection(Section section) {
        if(equalsUpStation(section)) {
            return this.getSections().stream()
                    .filter(comparedSection
                            -> comparedSection.getUpStationId().equals(section.getUpStationId()))
                    .findAny().orElseThrow(NullPointerException::new);
        }
        if(equalsDownStation(section)) {
            return this.getSections().stream()
                    .filter(comparedSection
                            -> comparedSection.getDownStationId().equals(section.getDownStationId()))
                    .findAny().orElseThrow(NullPointerException::new);
        }
        throw new NullPointerException();
    }

    /**
     * 해당 구간의 상행역이 등록된 구간들의 상행역 중에 있는지 여부
     * @param section
     * @return
     */
    private boolean existUpStations(Section section) {
        return this.sections.stream()
                .map(Section::getUpStationId)
                .anyMatch(upStationId -> upStationId.equals(section.getUpStationId()));
    }

    /**
     * 해당 구간의 하행역이 등록된 구간들의 하행역 중에 있는지 여부
     * @param section 
     * @return
     */
    private boolean existDownStations(Section section) {
        return this.sections.stream()
                .map(Section::getDownStationId)
                .anyMatch(downStationId -> downStationId.equals(section.getDownStationId()));
    }
}
