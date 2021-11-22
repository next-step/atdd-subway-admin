package nextstep.subway.section.domain;

import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class SectionSortSeq {

    private Integer sortSeq;

    protected SectionSortSeq() {
    }

    public SectionSortSeq(Integer sortSeq) {
        this.sortSeq = sortSeq;
    }

    public int compare(SectionSortSeq target) {
        if (sortSeq < target.sortSeq) {
            return -1;
        }

        if (sortSeq > target.sortSeq) {
            return  1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SectionSortSeq that = (SectionSortSeq) o;
        return Objects.equals(sortSeq, that.sortSeq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sortSeq);
    }

}
