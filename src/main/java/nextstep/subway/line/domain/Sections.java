package nextstep.subway.line.domain;

import nextstep.subway.Exception.CannotDeleteException;
import nextstep.subway.Exception.CannotUpdateSectionException;
import nextstep.subway.Exception.NotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    public static final int MINIMUM_SIZE_OF_SECTIONS = 1;
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    List<Station> getStationsInOrder() {
        List<Station> stations = new ArrayList<>();

        Station station = findFirstStation();
        stations.add(station);

        while (isExistNextSection(station)) {
            Section nextSection = findNextSection(station);
            station = nextSection.getDownStation();
            stations.add(station);
        }
        return stations;
    }

    private Station findFirstStation() {
        if (sections.isEmpty()) {
            throw new NotFoundException("지하철 구간이 등록되지 않았습니다.");
        }

        Station station = sections.get(0).getUpStation();
        while (isExistPreSection(station)) {
            Section section = findPreSection(station);
            station = section.getUpStation();
        }
        return station;
    }

    private boolean isExistPreSection(Station station) {
        return sections.stream()
                .anyMatch(section -> section.equalDownStation(station));
    }

    private Section findPreSection(Station station) {
        return sections.stream()
                .filter(section -> section.equalDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이전 구간이 없습니다."));
    }

    private boolean isExistNextSection(Station station) {
        return sections.stream()
                .anyMatch(section -> section.equalUpStation(station));
    }

    private Section findNextSection(Station station) {
        return sections.stream()
                .filter(section -> section.equalUpStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("다음 구간이 없습니다."));
    }

    void addSection(Section section) {
        if (this.sections.isEmpty()) {
            this.sections.add(section);
            return;
        }

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        int distance = section.getDistance();
        
        List<Station> stationsInOrder = getStationsInOrder();
        boolean existUpStation = stationsInOrder.stream().anyMatch(it -> it.equals(upStation));
        boolean existDownStation = stationsInOrder.stream().anyMatch(it -> it.equals(downStation));

        validStation(existUpStation, existDownStation);
        if (existUpStation) {
            updateUpStation(upStation, downStation, distance);
        }
        if (existDownStation) {
            updateDownStation(upStation, downStation, distance);
        }
        sections.add(section);
    }

    private void validStation(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new CannotUpdateSectionException("상행역과 하행역이 이미 노선에 모두 등록되어있어서 추가할 수 없습니다.");
        }
        if (!existUpStation && !existDownStation) {
            throw new CannotUpdateSectionException("상행역과 하행역 둘 중 하나도 포함되어있지 않아서 추가할 수 없습니다.");
        }
    }

    private void updateDownStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(it -> it.equalDownStation(downStation))
                .findFirst()
                .ifPresent(it -> it.updateDownStation(upStation, distance));
    }

    private void updateUpStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(it -> it.equalUpStation(upStation))
                .findFirst()
                .ifPresent(it -> it.updateUpStation(downStation, distance));
    }

    List<Section> getSections() {
        return this.sections;
    }

    void removeLineSection(Line line, Station station) {
        if (!getStationsInOrder().contains(station)) {
            throw new CannotDeleteException("노선에 등록되어있지 않은 역을 제거할 수 없습니다.");
        }

        if (sections.size() <= MINIMUM_SIZE_OF_SECTIONS) {
            throw new CannotDeleteException("구간이 하나인 노선에서 마지막 구간을 제거할 수 없습니다.");
        }

        Optional<Section> upStationSection = getUpStationSection(station);
        Optional<Section> downStationSection = getDownStationSection(station);

        if (upStationSection.isPresent() && downStationSection.isPresent()) {
            createSection(line, upStationSection.get(), downStationSection.get());
        }
        upStationSection.ifPresent(it -> sections.remove(it));
        downStationSection.ifPresent(it -> sections.remove(it));
    }

    private void createSection(Line line, Section upStationSection, Section downStationSection) {
        Station upStation = downStationSection.getUpStation();
        Station downStation = upStationSection.getDownStation();
        Distance newDistance = downStationSection.sumDistance(upStationSection);
        sections.add(new Section(line, upStation, downStation, newDistance.getDistance()));
    }

    private Optional<Section> getDownStationSection(Station station) {
        return sections.stream()
                .filter(section -> section.equalDownStation(station))
                .findFirst();
    }

    private Optional<Section> getUpStationSection(Station station) {
        return sections.stream()
                .filter(section -> section.equalUpStation(station))
                .findFirst();
    }
}
