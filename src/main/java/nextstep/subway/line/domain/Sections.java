package nextstep.subway.line.domain;

import nextstep.subway.line.ui.*;
import nextstep.subway.station.domain.Station;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true)
    @SortNatural
    private SortedSet<Section> sections = new TreeSet<>();

    public Sections() {
    }

    public void add(Section addedSection) {

        validateTwoStationAlreadyExist(addedSection);
        validateTwoStationNotExist(addedSection);

        changeStationInfoWhenUpStationMatch(addedSection);
        changeStationInfoWhenDownStationMatch(addedSection);

        sections.add(addedSection);
    }


    public void deleteStation(Station station) {

        validateLineContainsStation(station);
        validateSectionsHasOnlySection();

        //맨 앞역 삭제
        if (sections.first().getUpStation().equals(station)) {
            sections.remove(sections.first());
            return;
        }

        //맨 뒤역 삭제
        if (sections.last().getDownStation().equals(station)) {
            sections.remove(sections.last());
            return;
        }

        //중간역 삭제
        removeMiddleStation(station);
    }

    private void validateTwoStationAlreadyExist(Section addedSection) {
        boolean isDownStationExist = stations().contains(addedSection.getDownStation());
        boolean isUpStationExist = stations().contains(addedSection.getUpStation());

        if (isDownStationExist && isUpStationExist) {
            throw new TwoStationAlreadyExistException();
        }
    }

    private void changeStationInfoWhenUpStationMatch(Section addedSection) {
        sections.stream()
                .filter(section -> section.isEqualToUpStation(addedSection.getUpStation()))
                .findFirst()
                .ifPresent(findSection -> {
                    validateDistanceChange(findSection, addedSection);
                    findSection.changeUpStation(addedSection.getDownStation());
                    findSection.changeDistance(findSection.getDistance() - addedSection.getDistance());
                });
    }

    private void changeStationInfoWhenDownStationMatch(Section addedSection) {
        sections.stream()
                .filter(section -> section.isEqualToDownStation(addedSection.getDownStation()))
                .findFirst()
                .ifPresent(findSection -> {
                    validateDistanceChange(findSection, addedSection);
                    findSection.changeDownStation(addedSection.getUpStation());
                    findSection.changeDistance(findSection.getDistance() - addedSection.getDistance());
                });
    }

    private void validateTwoStationNotExist(Section addedSection) {

        if (stations().isEmpty()) {
            return;
        }

        boolean isDownStationExist = stations().contains(addedSection.getDownStation());
        boolean isUpStationExist = stations().contains(addedSection.getUpStation());

        if (!isDownStationExist && !isUpStationExist) {
            throw new TwoStationNotExistException();
        }
    }

    private void validateDistanceChange(Section section, Section addedSection) {
        if (section.getDistance() < addedSection.getDistance()) {
            throw new InvalidDistanceException();
        }
    }

    private void validateLineContainsStation(Station station) {
        if (!stations().contains(station)) {
            throw new NoStationInLineException();
        }
    }

    private void validateSectionsHasOnlySection() {
        if (sections.size() <= 1) {
            throw new CannotDeleteOnlySectionException();
        }
    }

    private void removeMiddleStation(Station station) {
        Section removedSection = sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Section changedSection = sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        sections.remove(removedSection);

        changedSection.changeDownStation(removedSection.getDownStation());
        changedSection.changeDistance(changedSection.getDistance() + removedSection.getDistance());
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();

        if (sections.size() == 0) {
            return stations;
        }

        stations.add(sections.first().getUpStation());
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }

        return stations;
    }
}
