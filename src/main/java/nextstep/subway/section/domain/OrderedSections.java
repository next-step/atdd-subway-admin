package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class OrderedSections {

    List<Section> orderedSections = new ArrayList<>();

    private OrderedSections(List<Section> orderedSections) {
        this.orderedSections = orderedSections;
    }

    public static OrderedSections of(List<Section> orderedSections) {
        return new OrderedSections(orderedSections);
    }

    public List<Section> get() {
        return orderedSections;
    }

    public void connectThroughOrderedSections(Section sectionIn) {
        int i = 0;
        boolean isConnected = false;
        while (!isConnected) {
            Section section = orderedSections.get(i);
            isConnected = runningThrough(sectionIn, section);
            ++i;
        }
    }

    private boolean runningThrough(Section sectionIn, Section section) {
//        Station upStationIn = sectionIn.getUpStation();
//        Station downStationIn = sectionIn.getDownStation();

        if (checkFront(sectionIn, section)
        || checkFrontMid(sectionIn, section)
        || checkRearMid(sectionIn, section)
        || checkRear(sectionIn, section)) {
            return true;
        }

//        if (downStationIn.equals(section.getUpStation())) {
//            return true;
//        }
//
//        if (upStationIn.equals(section.getUpStation())) {
//            validateDistance(sectionIn.getDistance(), section.getDistance());
//
//            section.setDistance(section.getDistance() - sectionIn.getDistance());
//            section.setUpStation(downStationIn);
//            return true;
//        }
//
//        if (downStationIn.equals(section.getDownStation())) {
//            validateDistance(sectionIn.getDistance(), section.getDistance());
//
//            section.setDistance(section.getDistance() - sectionIn.getDistance());
//            section.setDownStation(upStationIn);
//            return true;
//        }
//
//        if (upStationIn.equals(section.getDownStation())) {
//            return true;
//        }

        return false;
    }

    private boolean checkFront(Section sectionInput, Section sectionFromExist) {
        return sectionInput.getDownStation().equals(sectionFromExist.getUpStation());
    }

    private boolean checkFrontMid(Section sectionInput, Section sectionFromExist) {
        if (sectionInput.getUpStation().equals(sectionFromExist.getUpStation())) {
            validateDistance(sectionInput.getDistance(), sectionFromExist.getDistance());

            sectionFromExist.setDistance(sectionFromExist.getDistance() - sectionInput.getDistance());
            sectionFromExist.setUpStation(sectionInput.getDownStation());
            return true;
        }
        return false;
    }

    private boolean checkRearMid(Section sectionInput, Section sectionFromExist) {
        if (sectionFromExist.getDownStation().equals(sectionFromExist.getDownStation())) {
            validateDistance(sectionInput.getDistance(), sectionFromExist.getDistance());

            sectionFromExist.setDistance(sectionFromExist.getDistance() - sectionInput.getDistance());
            sectionFromExist.setDownStation(sectionInput.getUpStation());
            return true;
        }
        return false;
    }

    private boolean checkRear(Section sectionInput, Section sectionFromExist) {
        if (sectionInput.getUpStation().equals(sectionFromExist.getDownStation())) {
            return true;
        }
        return false;
    }

    private void validateDistance(int inputDistance, int existDistance) {
        if (inputDistance >= existDistance) {
            throw new IllegalArgumentException();
        }
    }

    private int absDiffDistance(Section section1, Section section2) {
        return -1;
    }
}
