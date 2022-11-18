package nextstep.subway.domain;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.value.ErrMsg;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = Lists.newArrayList();

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

    private void validateSection(Section section) {
        List<Station> stations = getAllStations();
        // 둘다 포함된 경우 에러
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())){
            throw new IllegalArgumentException(ErrMsg.SECTION_ALREADY_EXISTS);
        }
        // 둘다 없는 경우 에러
        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())){
            throw new IllegalArgumentException(ErrMsg.NO_MATCHING_STATIONS);
        }
    }

    private void addSectionInMiddle(Section section) {
        Optional<Section> downMatchingSection = getSectionSameDownStation(section.getDownStation());
        Optional<Section> upMatchingSection = getSectionSameUpStation(section.getUpStation());
        if (downMatchingSection.isPresent()){
            downMatchingSection.get().updateDownStation(section);
        }
        if(upMatchingSection.isPresent()){
            upMatchingSection.get().updateUpStation(section);
        }
        sections.add(section);

    }

    private Optional<Section> getSectionSameDownStation(Station station){
        return sections.stream().filter(section -> section.getDownStation().equals(station)).findFirst();
    }
    private Optional<Section> getSectionSameUpStation(Station station){
        return sections.stream().filter(section -> section.getUpStation().equals(station)).findFirst();
    }

    private boolean addFirstOrLastSection(Section section) {
        if(getFirstDownStation().equals(section.getUpStation())|| getLastUpStation().equals(section.getDownStation())){
            sections.add(section);
            return true;
        }
        return false;
    }

    private List<Station> getAllStations(){
        return sections.stream().map(Section::getStations).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    public List<Station> getOrderedStations(){
        Station currentStation = getLastUpStation();
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

    private Station getLastUpStation(){
        List<Station> stations = getUpStations();
        stations.removeAll(getDownStations());
        return stations.get(0);
    }

    private Station getFirstDownStation(){
        List<Station> stations = getDownStations();
        stations.removeAll(getUpStations());
        return stations.get(0);
    }

    public List<Section> getAll() {
        return sections;
    }
}
