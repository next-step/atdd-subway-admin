package nextstep.subway.domain;

import java.util.Comparator;

public class SectionOrderComparator implements Comparator<Section> {
    private static final int LESS_THAN = -1;
    private static final int EQUALS = 0;
    private static final int GREATER_THAN = 1;

    @Override
    public int compare(Section o1, Section o2) {
        if (o1.equalsOrder(o2)) {
            return EQUALS;
        }
        return o1.isPreOrder(o2) ? LESS_THAN : GREATER_THAN;
    }
}
