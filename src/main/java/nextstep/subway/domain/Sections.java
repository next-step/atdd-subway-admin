package nextstep.subway.domain;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.CannotDeleteException;
import nextstep.subway.value.ErrMsg;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = Lists.newArrayList();

    private static final int NON_DELETABLE_SECTION_COUNT = 1;

    public void add(Section section) {
        if(sections.isEmpty()){
            sections.add(section);
            return;
        }
        validateSection(section);
        if(!addFirstOrLastSection(section)){
            addSectionInMiddle(section);
        }
    }
    public List<Station> getOrderedStations(){
        Station currentStation = getFirstUpStation();
        List<Station> orderedStations = new ArrayList<>();
        orderedStations.add(currentStation);
        while (orderedStations.size()-1 < sections.size()){
            currentStation = matchingSection(currentStation).getDownStation();
            orderedStations.add(currentStation);
        }
        return orderedStations;
    }

    public Optional<Section> getSectionById(Long sectionId){
        return sections.stream().filter(section -> section.getId().equals(sectionId)).findFirst();
    }

    public List<Section> getAll() {
        return sections;
    }


    public void deleteStation(Station station) throws NotFoundException {
        validateDeleteCondition();
        if(!deleteFirstOrLastStation(station)){
            deleteMiddleStation(station);
        }
    }

    private void validateSection(Section section) {
        List<Station> stations = getAllStations();
        if (isContain(stations, section)){
            throw new IllegalArgumentException(ErrMsg.SECTION_ALREADY_EXISTS);
        }
        if (isNotContain(stations, section)){
            throw new IllegalArgumentException(ErrMsg.NO_MATCHING_STATIONS);
        }
    }
    private boolean isContain(List<Station> stations, Section section){
        return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
    }
    private boolean isNotContain(List<Station> stations, Section section){
        return !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation());
    }

    private void addSectionInMiddle(Section section) {
        Section matchingSection = getMatchingSection(section);
        if(isDownStationMatching(matchingSection, section)){
            matchingSection.updateDownStation(section);
            sections.add(section);
            return;
        }
        matchingSection.updateUpStation(section);
        sections.add(section);

    }

    private Section getMatchingSection(Section section){
        //앞의 로직상 이 구간은 null 불가능 -> 이때도 어떤 처리를 해줘야 하는가?
        return sections.stream().filter(it -> isMatchingSection(it, section)).findFirst().get();
    }
    private boolean isDownStationMatching(Section target, Section section){
        return target.getDownStation().equals(section.getDownStation());
    }
    private boolean isMatchingSection(Section target, Section section){
        return target.getUpStation().equals(section.getUpStation()) || target.getDownStation().equals(section.getDownStation());
    }

    private boolean addFirstOrLastSection(Section section) {
        if(isFirstOrLastFromSection(section)){
            sections.add(section);
            return true;
        }
        return false;
    }

    private boolean isFirstOrLastFromSection(Section section){
        return getLastDownStation().equals(section.getUpStation())|| getFirstUpStation().equals(section.getDownStation());
    }

    private List<Station> getAllStations(){
        return sections.stream().map(Section::getStations).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }



    private Section matchingSection(Station station){
        return sections.stream().filter(
                section -> section.getUpStation().equals(station)
        ).findFirst().get();
    }

    private List<Station> getUpStations(){
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }
    private List<Station> getDownStations(){
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private Station getFirstUpStation(){
        // 상행 종점 구하기
        List<Station> stations = getUpStations();
        stations.removeAll(getDownStations());
        return stations.get(0);
    }

    private Station getLastDownStation(){
        // 하행 종점 구하기
        List<Station> stations = getDownStations();
        stations.removeAll(getUpStations());
        return stations.get(0);
    }


    private void deleteMiddleStation(Station station) throws NotFoundException {
        Section targetSection = getSectionMatchingDownStation(station);
        Section deleteSection = getSectionMatchingUpStation(station);;
        targetSection.updateSectionInDelete(deleteSection);
        this.sections.remove(deleteSection);
    }

    private void validateDeleteCondition() {
        if(sections.size() <= NON_DELETABLE_SECTION_COUNT){
            throw new CannotDeleteException(ErrMsg.CANNOT_DELETE_SECTION_WHEN_ONE);
        }
    }

    private Section getSectionMatchingUpStation(Station station) throws NotFoundException {
        return sections.stream().filter(section -> section.getUpStation().equals(station)).findFirst().orElseThrow(
                ()-> new NotFoundException(ErrMsg.notFoundStation(station.getId()))
        );
    }
    private Section getSectionMatchingDownStation(Station station) throws NotFoundException {
        return sections.stream().filter(section -> section.getDownStation().equals(station)).findFirst().orElseThrow(
                ()-> new NotFoundException(ErrMsg.notFoundStation(station.getId()))
        );
    }

    private boolean deleteFirstOrLastStation(Station station) {
        if(getFirstUpStation().equals(station)){
            sections.remove(0);
            return true;
        }
        if(getLastDownStation().equals(station)){
            sections.remove(sections.size() -1);
            return true;
        }
        return false;
    }
}
