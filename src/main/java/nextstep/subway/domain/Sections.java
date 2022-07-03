package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isEmpty() {
        return sections.size() == 0;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        AtomicBoolean isSelected = new AtomicBoolean(true);
        AtomicReference<Station> station = new AtomicReference<>(findUpStation());
        stations.add(station.get());

        while (isSelected.get()) {
            isSelected.set(false);
            sections.stream()
                    .filter(section -> section.hasUpStation(station.get()))
                    .findFirst()
                    .ifPresent(section -> {
                        station.set(section.getDownStation());
                        stations.add(station.get());
                        isSelected.set(true);
                    });
        }

        return stations;
    }

    public Station findUpStation() {
        AtomicBoolean isSelected = new AtomicBoolean(true);
        AtomicReference<Station> station = new AtomicReference<>(sections.get(0).getUpStation());

        while (isSelected.get()) {
            isSelected.set(false);
            sections.stream()
                    .filter(section -> section.hasDownStation(station.get()))
                    .findFirst()
                    .ifPresent(section -> {
                        station.set(section.getUpStation());
                        isSelected.set(true);
                    });
        }

        return station.get();
    }

    public void addSection(Section section) {
        if (isEmpty()) {
            sections.add(section);
            return;
        }

        List<Station> stations = getStations();
        boolean isUpStationExisted = isUpStationExisted(stations, section);
        boolean isDownStationExisted = isDownStationExisted(stations, section);
        isRegisteredInLine(isUpStationExisted, isDownStationExisted);
        canRegisterToLine(stations, section);

        if (isUpStationExisted) {
            updateUpStation(section);
        }
        if (isDownStationExisted) {
            updateDownStation(section);
        }
        sections.add(section);
    }

    public boolean isUpStationExisted(List<Station> stations, Section section) {
        return stations.stream().anyMatch(section::hasUpStation);
    }

    public boolean isDownStationExisted(List<Station> stations, Section section) {
        return stations.stream().anyMatch(section::hasDownStation);
    }

    public void isRegisteredInLine(boolean isUpStationExisted, boolean isDownStationExisted) {
        if (isUpStationExisted && isDownStationExisted) {
            throw new IllegalArgumentException("이미 등록된 구간 입니다.");
        }
    }

    public void canRegisterToLine(List<Station> stations, Section section) {
        if (!stations.isEmpty()
                && stations.stream().noneMatch(section::hasUpStation)
                && stations.stream().noneMatch(section::hasDownStation)) {
            throw new IllegalArgumentException("등록할 수 없는 구간 입니다.");
        }
    }

    public void updateUpStation(Section newSection) {
        sections.stream()
                .filter(section -> newSection.hasUpStation(section.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpStationReducedDistance(newSection));
    }

    public void updateDownStation(Section newSection) {
        sections.stream()
                .filter(section -> newSection.hasDownStation(section.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateDownStationReducedDistance(newSection));
    }

    public void removeSection(Station station) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("마지막 구간은 삭제할 수 없습니다.");
        }
        if (getStations().stream().noneMatch(st -> st.equals(station))) {
            throw new IllegalArgumentException(String.format("%s 역은 노선에 존재하지 않습니다.", station.getName()));
        }

        if (isUpStationInSection(station)) {
            removeUpStationInSection(station);
            return;
        }
        removeDownStationInSection(station);
    }

    public boolean isUpStationInSection(Station station) {
        return sections.stream().anyMatch(section -> section.getUpStation().equals(station));
    }

    public void removeUpStationInSection(Station station) {
        Section section = sections.stream()
                                  .filter(sec -> sec.hasUpStation(station))
                                  .findFirst()
                                  .orElseThrow(NoSuchElementException::new);
        sections.stream()
                .filter(sec -> sec.hasDownStation(station))
                .findFirst()
                .ifPresent(sec -> sec.updateDownStationIncreasedDistance(section));
        sections.remove(section);
    }

    public void removeDownStationInSection(Station station) {
        Section section = sections.stream()
                                  .filter(sec -> sec.hasDownStation(station))
                                  .findFirst().orElseThrow(NoSuchElementException::new);
        sections.stream()
                .filter(sec -> sec.hasUpStation(station))
                .findFirst()
                .ifPresent(sec -> sec.updateUpStationInSection(section));
        sections.remove(section);
    }
}
