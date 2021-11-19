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
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    private class SplitSectionItem {
        int index;
        Section newUpSection;
        Section newDownSection;

        public SplitSectionItem(int index, Section newUpSection, Section newDownSection) {
        this.index = index;
        this.newUpSection = newUpSection;
        this.newDownSection = newDownSection;
        }
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

    public void add(Section section) {
        if (this.values.isEmpty()) {
            this.values.add(section);
            return;
        }

        validation(section);

        SplitSectionItem splitSectionItem = getSplitSectionItem(section).orElseThrow();

        int index = splitSectionItem.index;

        this.values.remove(index);
        this.values.add(index, splitSectionItem.newDownSection);
        this.values.add(index, splitSectionItem.newUpSection);
    }

    private void validation(Section section) {
        checkContainStaion(section);
        checkDistance(section);
    }

    private Optional<SplitSectionItem> getSplitSectionItem(Section section) {
        Optional<SplitSectionItem> upStationTerminalSplitItem = generateUpStationTerminalSplitItem(section);

        if (upStationTerminalSplitItem.isPresent()) {
            return upStationTerminalSplitItem;
        }

        Optional<SplitSectionItem> upStationSplitItem = generateUpStationSplitItem(section);

        if (upStationSplitItem.isPresent()) {
            return upStationSplitItem;
        }

        Optional<SplitSectionItem> downStationSplitItem = generateDownStationSplitItem(section);


        if (downStationSplitItem.isPresent()) {
            return downStationSplitItem;
        }

        Optional<SplitSectionItem> downStationTerminalSplitItem = generateDownStationTerminalSplitItem(section);

        if (downStationTerminalSplitItem.isPresent()) {
            return downStationTerminalSplitItem;
        }

        return Optional.empty();
    }

    private Optional<SplitSectionItem> generateDownStationTerminalSplitItem(Section section) {
        return this.values.stream()
                    .filter(sectionItem -> sectionItem.getDownStation().equals(section.getUpStation()))
                    .findFirst()
                    .map(findSection -> new SplitSectionItem(this.values.indexOf(findSection),
                                                            Section.valueOf(findSection.getUpStation(), findSection.getDownStation(), findSection.getDistance()),
                                                            Section.valueOf(section.getUpStation(), section.getDownStation(), section.getDistance())));
    }

    private Optional<SplitSectionItem> generateDownStationSplitItem(Section section) {
        return this.values.stream()
                    .filter(sectionItem -> sectionItem.getDownStation().equals(section.getDownStation()))
                    .findFirst()
                    .map(findSection -> new SplitSectionItem(this.values.indexOf(findSection),
                                                            Section.valueOf(findSection.getUpStation(), section.getUpStation(), findSection.getDistance().minus(section.getDistance())),
                                                            Section.valueOf(section.getUpStation(), findSection.getDownStation(), section.getDistance())));
    }

    private Optional<SplitSectionItem> generateUpStationSplitItem(Section section) {
        return this.values.stream()
                    .filter(sectionItem -> sectionItem.getUpStation().equals(section.getUpStation()))
                    .findFirst()
                    .map(findSection -> new SplitSectionItem(this.values.indexOf(findSection),
                                                            Section.valueOf(findSection.getUpStation(), section.getDownStation(), findSection.getDistance().minus(section.getDistance())),
                                                            Section.valueOf(section.getDownStation(), findSection.getDownStation(), section.getDistance())));
    }

    private Optional<SplitSectionItem> generateUpStationTerminalSplitItem(Section section) {
        return this.values.stream()
                    .filter(sectionItem -> sectionItem.getUpStation().equals(section.getDownStation()))
                    .findFirst()
                    .map(findSection -> new SplitSectionItem(this.values.indexOf(findSection),
                                                    Section.valueOf(section.getUpStation(), section.getDownStation(), section.getDistance()),
                                                    Section.valueOf(findSection.getUpStation(), findSection.getDownStation(), findSection.getDistance())));
    }

    private void checkDistance(Section section) {
        if (isUpStationTeminal(section)) {
            return;
        }

        if (isDownStaionTeminal(section)) {
            return;
        }

        Optional<Section> upStationMaption = values.stream()
                                            .filter(item-> item.getDownStation().equals(section.getDownStation()) || item.getUpStation().equals(section.getUpStation()))
                                            .findFirst();

        Section upStationMatchSection = upStationMaption.orElseThrow();

        upStationMatchSection.getId();
    }

    private boolean isDownStaionTeminal(Section section) {
        return this.values.get(this.values.size() - 1).getDownStation().equals(section.getUpStation());
    }

    private boolean isUpStationTeminal(Section section) {
        return this.values.get(0).getUpStation().equals(section.getDownStation());
    }

    private void checkContainStaion(Section section) {
        if (isUpStationTeminal(section)) {
            return;
        }

        if (isDownStaionTeminal(section)) {
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
