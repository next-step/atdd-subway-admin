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

        for (Section section : sections) {
            //중간(앞 역 일치)
            if (section.isEqualToUpStation(addedSection.getUpStation())) {
                validateDistanceChange(section, addedSection);
                section.changeUpStation(addedSection.getDownStation());
                section.changeDistance(section.getDistance() - addedSection.getDistance());

                break;
            }

            //중간(뒤 역 일치)
            if (section.isEqualToDownStation(addedSection.getDownStation())) {
                validateDistanceChange(section, addedSection);
                section.changeDownStation(addedSection.getUpStation());
                section.changeDistance(section.getDistance() - addedSection.getDistance());

                break;
            }
        }

        sections.add(addedSection);
    }

    private void validateTwoStationAlreadyExist(Section addedSection) {
        boolean isDownStationExist = stations().contains(addedSection.getDownStation());
        boolean isUpStationExist = stations().contains(addedSection.getUpStation());

        if (isDownStationExist && isUpStationExist) {
            throw new TwoStationAlreadyExistException();
        }
    }

    private void validateTwoStationNotExist(Section addedSection) {

        if (stations().isEmpty()) {
            return;
        }

        boolean isDownStationExist = !stations().contains(addedSection.getDownStation());
        boolean isUpStationExist = !stations().contains(addedSection.getUpStation());

        if (isDownStationExist && isUpStationExist) {
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
