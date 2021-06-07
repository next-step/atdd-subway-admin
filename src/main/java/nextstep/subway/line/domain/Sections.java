package nextstep.subway.line.domain;

import nextstep.subway.line.ui.InvalidDistanceException;
import nextstep.subway.line.ui.TwoStationAlreadyExistException;
import nextstep.subway.line.ui.TwoStationNotExistException;
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
