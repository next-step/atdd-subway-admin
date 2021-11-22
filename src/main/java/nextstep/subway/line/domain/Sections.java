package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
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
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public Section findFirstItem() {
        return this.values.get(0);
    }

    public Section findLastItem() {
        return this.values.get(this.values.size() - 1);
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

        adjustSection(section);
    }

    private void adjustSection(Section section) {
        this.values.stream()
                   .filter(findSection -> findSection.hasStaion(section))
                   .findFirst()
                   .ifPresent(findSection -> {
                        splitSection(findSection, section);
                    });
    }

    private void splitSection(Section findSection, Section section) {
        SplitedSectionsFactory.generate(findSection.findMatchingType(section), findSection, section)
                        .ifPresent(newSections -> { 
                            int index = this.values.indexOf(findSection);
                        
                            this.values.remove(index);
                            this.values.addAll(index, newSections.values);
                        });
    }

    private void validation(Section section) {
        checkContainStaion(section);
        checkDistance(section);
    }

    private void checkDistance(Section section) {
        if (isUpStationTeminal(section) || isDownStaionTeminal(section)) {
            return;
        }

        values.stream().filter(findSection -> isNotTerminalStationMatching(findSection, section))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("일치하는 구간이 존재하지 않습니다."));
    }

    private boolean isNotTerminalStationMatching(Section findSection, Section section) {
        return findSection.isEqualDownStation(section.getDownStation()) || findSection.isEqualUpStation(section.getUpStation());
    }

    private boolean isDownStaionTeminal(Section section) {
        return this.values.get(this.values.size() - 1)
                            .isEqualDownStation(section.getUpStation());
    }

    private boolean isUpStationTeminal(Section section) {
        return this.values.get(0)
                            .isEqualUpStation(section.getDownStation());
    }

    private void checkContainStaion(Section section) {
        if (isUpStationTeminal(section)) {
            return;
        }

        if (isDownStaionTeminal(section)) {
            return;
        }

        Optional<Section> upStationMatching = values.stream()
                                                    .filter(item -> item.isEqualUpStation(section.getUpStation()))
                                                    .findFirst();

        Optional<Section> downStationMatching = values.stream()
                                                    .filter(item -> item.isEqualDownStation(section.getDownStation()))
                                                    .findFirst();

        checkNotContainStations(upStationMatching, downStationMatching);
        checkAllContainStations(upStationMatching, downStationMatching);
    }

    private void checkAllContainStations(Optional<Section> upStationMaption, Optional<Section> downStationMaption) {
        if (!upStationMaption.isEmpty() && !downStationMaption.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    private void checkNotContainStations(Optional<Section> upStationMaption, Optional<Section> downStationMaption) {
        if (upStationMaption.isEmpty() && downStationMaption.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public void deleteSection(Long stationId) {
        validateDelete();

        Section findSection2 = this.values.stream()
                                        .filter(findSection -> findSection.getUpStation().getId() == stationId)
                                        .findFirst()
                                        .orElse(Section.valueOf(null, null, null));
                                        //.orElseThrow(() -> new NoSuchElementException("조회되는 구간이 없습니다."));

        int index = 0;

        index = this.values.indexOf(findSection2);

        if (this.values.get(this.values.size() - 1).getDownStation().getId().equals(stationId)) {
            index = this.values.size() - 1; 
            Section deletedSection = this.values.get(index);

            this.values.get(index - 1).plusDistance(deletedSection);
            this.values.get(index - 1).updateDownStation(deletedSection.getUpStation());

            this.values.remove(deletedSection);
            return;
        }

        if (index == 0) {
            this.values.remove(index);
        } else {
            Section deletedSection = this.values.get(index);

            this.values.get(index - 1).plusDistance(deletedSection);
            this.values.get(index - 1).updateDownStation(deletedSection.getDownStation());
            this.values.remove(deletedSection);
        }
    }

    private void validateDelete() {
        if (this.values.size() <= 1) {
            throw new IllegalArgumentException("구간이 1개 이하인 라인의 종점역은 삭제할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
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
