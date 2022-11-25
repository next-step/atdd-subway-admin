package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 노선 구간 목록을 관리하는 일급 컬렉션
 */
@Embeddable
public class SectionLineUp {

    private static final int START_INDEX = 0;

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Section> sectionList = new ArrayList<>();

    protected SectionLineUp() {
    }

    public List<Section> getSectionList(Comparator<Section> comparator) {
        sectionList.sort(comparator);
        return sectionList;
    }

    public void addSection(Section section) {
        if (sectionList.isEmpty()) {
            section.updateOrder(new Order(START_INDEX));
            sectionList.add(section);
            return;
        }
        validUnknownStation(section);
        validSameSection(section);
        add(section);
    }

    private void add(Section section) {
        boolean isEndUpStation = isEndUpStation(section);
        boolean isEndDownStation = isEndDownStation(section);
        if (isEndUpStation) {
            addEndUpStation(section);
            return;
        }
        if (isEndDownStation) {
            addEndDownStation(section);
            return;
        }
        createInternal(section);
    }

    private boolean isEndUpStation(Section section) {
        //  출발지가 같은 노선이 없고, 도착지에서 출발하는 노선은 있지만, 도착지로 향하는 노선이 없는 경우
        return notHasSameUpStation(section) && notHasSameDownStation(section) && hasSameUpStationByDownStation(section);
    }

    private boolean isEndDownStation(Section section) {
        //  출발지로 향하는 노선이 있지만, 출발지에서 출발하는 노선이 없는 경우
        return hasSameDownStationByUpStation(section) && notHasSameUpStation(section);
    }

    private boolean notHasSameDownStation(Section section) {
        return sectionList.stream().noneMatch(streamSection -> streamSection.isSameDownStation(section));
    }

    private boolean hasSameUpStationByDownStation(Section section) {
        return sectionList.stream()
                .anyMatch(streamSection -> streamSection.isSameUpStationId(section.getDownStationId()));
    }
    private boolean notHasSameUpStation(Section section) {
        return sectionList.stream().noneMatch(streamSection -> streamSection.isSameUpStation(section));
    }


    private boolean hasSameDownStationByUpStation(Section section) {
        return sectionList.stream()
                .anyMatch(streamSection -> streamSection.isSameDownStationId(section.getUpStationId()));
    }

    private void addEndUpStation(Section section) {
        section.updateOrder(new Order(START_INDEX));
        sectionList.stream().filter(section::isEqualsOrPreOrder)
                .forEach(Section::plusOrder);
        sectionList.add(section);
    }

    private void addEndDownStation(Section section) {
        final int maxIndex = sectionList.stream().mapToInt(Section::getOrderNumber).max().orElse(START_INDEX);
        section.updateOrder(new Order(maxIndex + 1));
        sectionList.add(section);
    }

    private void createInternal(Section section) {
        if (isInternalUpStation(section)) {
            createInternalUpStation(section);
            return;
        }
        if (isInternalDownStation(section)) {
            createInternalDownStation(section);
        }
    }

    private boolean isInternalUpStation(Section section) {
        return sectionList.stream().anyMatch(streamSection -> streamSection.isSameUpStation(section));
    }

    private boolean isInternalDownStation(Section section) {
        return sectionList.stream().anyMatch(streamSection -> streamSection.isSameDownStation(section));
    }

    private void createInternalUpStation(Section section) {
        sectionList.stream().filter(streamSection -> streamSection.isSameUpStation(section))
                .findFirst()
                .ifPresent(existSection -> {
                    existSection.updateDistance(existSection.minusDistance(section.getDistance()));
                    existSection.updateUpStationId(section.getDownStationId());
                    section.updateOrder(existSection.getOrder());
                    sectionList.stream().filter(streamSection -> streamSection.isEqualsOrPreOrder(existSection))
                            .forEach(Section::plusOrder);
                    sectionList.add(section);
                });
    }

    private void createInternalDownStation(Section section) {
        sectionList.stream().filter(streamSection -> streamSection.isSameDownStation(section))
                .findFirst()
                .ifPresent(existSection -> {
                    existSection.updateDistance(existSection.minusDistance(section.getDistance()));
                    existSection.updateDownStationId(section.getUpStationId());
                    section.updateOrder(existSection.getOrder().plusOne());
                    sectionList.stream().filter(section::isEqualsOrLastOrder)
                            .forEach(Section::plusOrder);
                    sectionList.add(section);
                });
    }

    private void validUnknownStation(Section section) {
        if (sectionList.stream().noneMatch(streamSection -> streamSection.isKnownSection(section))) {
            throw new IllegalArgumentException("상행역, 하행역이 노선에 포함되어 있지 않습니다. 상행역ID:" + section.getUpStationId() +
                    ", 하행역ID:" + section.getDownStationId());
        }
    }

    private void validSameSection(Section section) {
        if (sectionList.stream()
                .anyMatch(streamSection -> streamSection.isKnownStationId(section.getUpStationId())) &&
                sectionList.stream()
                        .anyMatch(streamSection -> streamSection.isKnownStationId(section.getDownStationId()))) {
            throw new IllegalArgumentException(
                    "이미 중복된 구간이 있습니다. 상행선id:" + section.getUpStationId()
                            + ", 하행선id:" + section.getDownStationId());
        }
    }

    public List<Long> getStationIds(Comparator<Section> comparator) {
        sectionList.sort(comparator);
        final List<Long> stationIds = new ArrayList<>();
        sectionList.forEach(section -> {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        });
        return stationIds.stream().distinct().collect(Collectors.toList());
    }
}
