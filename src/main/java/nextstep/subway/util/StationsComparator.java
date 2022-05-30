package nextstep.subway.util;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.Comparator;

public class StationsComparator implements Comparator<Section> {
    @Override
    public int compare(Section o1, Section o2) {
        Station s1_downStation = o1.getDownStation();
        Station s2_upStation = o2.getUpStation();

        if (!s1_downStation.getName().equals(s2_upStation.getName())) {
            return 1;
        } else {
            return -1;
        }
    }

}
