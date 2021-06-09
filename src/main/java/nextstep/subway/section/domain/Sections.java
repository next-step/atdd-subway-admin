package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.hibernate.annotations.SortNatural;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @SortNatural
    private SortedSet<Section> sections = new TreeSet<>();

    public SortedSet<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        validationStation(section);
        addIfMatchUpStation(section);
        addIfMatchDownStation(section);

        this.sections.add(section);
    }

    private void validationStation(Section addSection) {
        if (sections.isEmpty()) {
            return;
        }
        Set<Station> stations = totalStationSet();
        boolean existUpStation = stations.contains(addSection.getUpStation());
        boolean existDownStation = stations.contains(addSection.getDownStation());

        if (!(existUpStation ^ existDownStation)) {
            throw new IllegalArgumentException("노선에 역이 모두 존재하지 않거나 이미 등록된 역입니다.");
        }
    }

    private Set<Station> totalStationSet() {
        Set<Station> stationSet = sections.stream()
                .map(section -> section.getUpStation())
                .collect(Collectors.toSet());

        Set<Station> downStationSet = sections.stream()
                .map(section -> section.getDownStation())
                .collect(Collectors.toSet());

        stationSet.addAll(downStationSet);

        return stationSet;
    }

    private void addIfMatchUpStation(Section addSection) {
        Optional<Section> matchUpstation = getNextSection(addSection.getUpStation());

        if (matchUpstation.isPresent()) {
            Section section = matchUpstation.get();

            section.changeUpSection(addSection);
        }
    }

    private void addIfMatchDownStation(Section addSection) {
        Optional<Section> matchDownStation = getPrevSection(addSection.getDownStation());

        if (matchDownStation.isPresent()) {
            Section section = matchDownStation.get();

            section.changeDownSection(addSection);
        }
    }

    public void delete(Station station) {
        if (sections.size() < 2 ) {
            throw new IndexOutOfBoundsException("노선에 역이 2개 이하 일때는 구간 삭제가 불가능합니다.");
        }

        Optional<Section> prevSection = getPrevSection(station);
        Optional<Section> nextSection = getNextSection(station);

        mergeIfBetweenSection(prevSection, nextSection);
        deleteSections(prevSection, nextSection);
    }

    private void mergeIfBetweenSection(Optional<Section> optionalPrevSection, Optional<Section> optionalNextSection) {
        if(optionalPrevSection.isPresent() && optionalNextSection.isPresent()) {
            Section prevSection = optionalPrevSection.get();
            Section nextSection = optionalNextSection.get();
            prevSection.mergeSection(nextSection);
        }
    }

    private void deleteSections(Optional<Section> optionalPrevSection, Optional<Section> optionalNextSection) {
        if(optionalNextSection.isPresent()) {
            sections.remove(optionalNextSection.get());
        }

        if(optionalPrevSection.isPresent() && !optionalNextSection.isPresent()) {
            sections.remove(optionalPrevSection.get());
        }
    }

    private Optional<Section> getNextSection(Station station) {
        return sections.stream()
                .filter(section -> section.equalsUpStation(station))
                .findFirst();
    }

    private Optional<Section> getPrevSection(Station station) {
        return sections.stream()
                .filter(section -> section.equalsDownStation(station))
                .findFirst();
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(sections.first().getUpStation());

        for (Section section : sections) {
            stations.add(section.getDownStation());
        }

        return stations;
    }
}
