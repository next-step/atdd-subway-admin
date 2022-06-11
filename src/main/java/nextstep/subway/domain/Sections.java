package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.InvalidSectionException;
import nextstep.subway.exception.SectionDeleteException;

@Embeddable
public class Sections {

    private static final int MIN_SIZE = 1;
    private static final String STATION_PAIR_BOTH_EXISTS = "상행역(%s)과 하행역(%s)이 모두 노선에 등록되어 있으면 구간을 추가할 수 없습니다.";
    private static final String STATION_PAIR_NONE_EXISTS = "상행역(%s), 하행역(%s) 중 최소한 하나의 역이 등록되어 있어야 합니다.";
    private static final String STATION_NOT_EXISTS = "존재하지 않는 지하철 역 입니다.";
    private static final String LAST_SECTION = "구간이 하나인 노선에서 마지막 구간을 제거할 수 없습니다.";
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section section) {
        update(section);
        sections.add(section);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public void validateDuplicate(Section target) {
        Optional<Section> optionalSection = sections.stream()
            .filter(element -> element.isSameStationPair(target))
            .findFirst();

        if (optionalSection.isPresent()) {
            throw new InvalidSectionException(
                String.format(STATION_PAIR_BOTH_EXISTS, target.getUpStation(),
                    target.getDownStation()));
        }
    }

    public void validateExistence(Section target) {
        if (!isExistStation(target.getDownStation())
            && !isExistStation(target.getUpStation())) {
            throw new InvalidSectionException(
                String.format(STATION_PAIR_NONE_EXISTS, target.getUpStation(),
                    target.getDownStation()));
        }
    }

    private boolean isExistStation(Station target) {
        Set<Station> stations = findAllStations();
        return stations.contains(target);
    }

    private Set<Station> findAllStations() {
        return sections.stream().
            flatMap(s -> s.getStationPair().stream()).
            collect(Collectors.toSet());
    }

    private void update(Section target) {
        for (Section section : sections) {
            section.update(target);
        }
    }

    public List<Station> stations() {
        return sections.stream()
            .map(Section::getStationPair)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void deleteStation(Station station) {
        Optional<Section> optionalDownSection = findSectionByUpStation(station);
        Optional<Section> optionalUpSection = findSectionByDownStation(station);

        boolean isUpSectionPresent = optionalUpSection.isPresent();
        boolean isDownSectionPresent = optionalDownSection.isPresent();

        validateSectionNull(isUpSectionPresent, isDownSectionPresent);
        validatesSectionsSize();

        deleteOptionalSection(optionalUpSection);
        deleteOptionalSection(optionalDownSection);

        if(isUpSectionPresent && isDownSectionPresent){
            combineSection(optionalUpSection.get(), optionalDownSection.get());
        }
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return this.sections
            .stream()
            .filter(section -> section.equalsUpStation(upStation))
            .findFirst();
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return this.sections
            .stream()
            .filter(section -> section.equalsDownStation(downStation))
            .findFirst();
    }

    private void validateSectionNull(boolean containsUpStation, boolean containsDownStation){
        if (!containsUpStation && !containsDownStation) {
            throw new SectionDeleteException(STATION_NOT_EXISTS);
        }
    }

    private void validatesSectionsSize(){
        if (sections.size() <= MIN_SIZE) {
            throw new SectionDeleteException(LAST_SECTION);
        }
    }

    private void deleteOptionalSection(Optional<Section> optionalSection) {
        if (optionalSection.isPresent()) {
            Section section = optionalSection.get();
            sections.remove(section);
        }
    }

    private void combineSection(Section upSection, Section downSection) {
        Distance newDistance = Section.newDistance(upSection, downSection);
        Section newSection = Section.of(upSection.getUpStation(), downSection.getDownStation(), newDistance);
        newSection.setLine(upSection.getLine());
        addSection(newSection);
    }

}
