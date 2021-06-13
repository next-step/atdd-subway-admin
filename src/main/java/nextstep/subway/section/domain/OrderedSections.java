package nextstep.subway.section.domain;

import java.util.*;

public class OrderedSections {

    private List<Section> orderedSections;

    private OrderedSections(List<Section> orderedSections) {
        this.orderedSections = orderedSections;
        makeOrderedSectionsFromTop(findTopSection());
    }

    public static OrderedSections of(List<Section> orderedSections) {
        return new OrderedSections(orderedSections);
    }

    public List<Section> get() {
        return Collections.unmodifiableList(orderedSections);
    }

    protected Section findTopSection() {
        return orderedSections.stream()
                .filter(section -> isTop(section))
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    private boolean isTop(Section section) {
        return orderedSections.stream()
                .noneMatch(it -> it.isInFrontOf(section));
    }

    private void makeOrderedSectionsFromTop(Section topSection) {
        List<Section> tempSections = new ArrayList<>();
        tempSections.add(topSection);

        Map<String, Section> stationMap = makeStationMap();

        while (topSection != null) {
            topSection = stationMap.get(topSection.downStationName());
            addNextSection(tempSections, topSection);
        }

        this.orderedSections = tempSections;
    }

    private Map<String, Section> makeStationMap() {
        Map<String, Section> sectionMap = new HashMap<>();
        for (Section it : orderedSections) {
            sectionMap.put(it.upStationName(), it);
        }
        return sectionMap;
    }

    private void addNextSection(List<Section> orderedSections, Section section) {
        if (section == null) {
            return;
        }
        if (orderedSections.contains(section)) {
            return;
        }
        orderedSections.add(section);
    }

}
