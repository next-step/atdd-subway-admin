package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id", cascade = CascadeType.ALL)
    private List<Section> values;

    protected Sections() {

    }

    private Sections(List<Section> sections) {
        this.values = sections;
    }

    public static Sections valueOf(Section ... sections) {
        return new Sections(new ArrayList<>(Arrays.asList(sections)));
    }

    public static Sections valueOf(List<Section> sections) {
        return new Sections(sections);
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public Section get(int index) {
        return this.values.get(index);
    }

    public Integer size() {
        return this.values.size();
    }

    public List<Station> findUpStations() {
        return this.values.stream()
                        .map(Section::getUpStation)
                        .collect(Collectors.toList());
    }
    class AddSectionItem {
       int index;
       Section newUpSection;
       Section newDownSection;

       public AddSectionItem(int index, Section newUpSection, Section newDownSection) {
        this.index = index;
        this.newUpSection = newUpSection;
        this.newDownSection = newDownSection;
       }
    }

    public void add(Section section) {
        if (this.values.isEmpty()) {
            this.values.add(section);
            return;
        }

        validateContain(section);
        validateDistance(section);

        AddSectionItem addSectionItem = getAddSectionItem(section).orElseThrow();

        int index = addSectionItem.index;
        this.values.remove(index);
        this.values.add(index, addSectionItem.newDownSection);
        this.values.add(index, addSectionItem.newUpSection);
    }

    private Optional<AddSectionItem> getAddSectionItem(Section section) {
        // 상행 종점 추가
        Optional<AddSectionItem> addSectionItem1 =  this.values.stream()
                    .filter(sectionItem -> sectionItem.getUpStation().equals(section.getDownStation()))
                    .findFirst()
                    .map(findSection -> new AddSectionItem(this.values.indexOf(findSection), 
                                                    Section.valueOf(section.getUpStation(), section.getDownStation(), section.getDistance()), 
                                                    Section.valueOf(findSection.getUpStation(), findSection.getDownStation(), findSection.getDistance())) );

        if (addSectionItem1.isPresent()) {
            return addSectionItem1;
        }

        // 상행 매핑 추가
        Optional<AddSectionItem> addSectionItem2 = this.values.stream()
                    .filter(sectionItem -> sectionItem.getUpStation().equals(section.getUpStation()))
                    .findFirst()
                    .map(findSection -> new AddSectionItem(this.values.indexOf(findSection), 
                                                            Section.valueOf(findSection.getUpStation(), section.getDownStation(), findSection.getDistance().minus(section.getDistance())), 
                                                            Section.valueOf(section.getDownStation(), findSection.getDownStation(), section.getDistance())));

        if (addSectionItem2.isPresent()) {
            return addSectionItem2;
        }
        
        // 하행 매핑 추가
        Optional<AddSectionItem> addSectionItem3 = this.values.stream()
                    .filter(sectionItem -> sectionItem.getDownStation().equals(section.getDownStation()))
                    .findFirst()
                    .map(findSection -> new AddSectionItem(this.values.indexOf(findSection), 
                                                            Section.valueOf(findSection.getUpStation(), section.getUpStation(), findSection.getDistance().minus(section.getDistance())), 
                                                            Section.valueOf(section.getUpStation(), findSection.getDownStation(), section.getDistance())));


        if (addSectionItem3.isPresent()) {
            return addSectionItem3;
        }
        // 하행 종점 추가
        Optional<AddSectionItem> addSectionItem4 = this.values.stream()
                    .filter(sectionItem -> sectionItem.getDownStation().equals(section.getUpStation()))
                    .findFirst()
                    .map(findSection -> new AddSectionItem(this.values.indexOf(findSection), 
                                                            Section.valueOf(findSection.getUpStation(), findSection.getDownStation(), findSection.getDistance()),
                                                            Section.valueOf(section.getUpStation(), section.getDownStation(), section.getDistance()))
                    );

        if (addSectionItem4.isPresent()) {
            return addSectionItem4;
        }

        return Optional.empty();
    }

    private void validateDistance(Section section) {
        if (this.values.get(0).getUpStation().equals(section.getDownStation())) {
            return;
        }

        if (this.values.get(this.values.size() - 1).getDownStation().equals(section.getUpStation())) {
            return;
        }

        Optional<Section> upStationMaption = values.stream()
                                            .filter(item-> item.getDownStation().equals(section.getDownStation()) || item.getUpStation().equals(section.getUpStation()))
                                            .findFirst();

        Section upStationMatchSection = upStationMaption.orElseThrow();

        upStationMatchSection.getId();
    }

    private void validateContain(Section section) {
        if (this.values.get(0).getUpStation().equals(section.getDownStation())) {
            return;
        }

        if (this.values.get(this.values.size() - 1).getDownStation().equals(section.getUpStation())) {
            return;
        }

        Optional<Section> upStationMaption = values.stream()
                                            .filter(item-> item.getUpStation().equals(section.getUpStation()))
                                            .findFirst();

        Optional<Section> downStationMaption = values.stream()
                                            .filter(item-> item.getDownStation().equals(section.getDownStation()))
                                            .findFirst();

        checkNotContainStations(upStationMaption, downStationMaption);
        checkAllContainStations(upStationMaption, downStationMaption);
    }

    private void checkAllContainStations(Optional<Section> upStationMaption, Optional<Section> downStationMaption) {
        if(!upStationMaption.isEmpty() && !downStationMaption.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    private void checkNotContainStations(Optional<Section> upStationMaption, Optional<Section> downStationMaption) {
        if(upStationMaption.isEmpty() && downStationMaption.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Sections)) {
            return false;
        }
        Sections sections = (Sections) o;
        return Objects.equals(this.values, sections.values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(values);
    }
}
