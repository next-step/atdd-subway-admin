package nextstep.subway.line.dto;

import lombok.Getter;
import lombok.Setter;
import nextstep.subway.station.domain.Station;

import java.util.Objects;

@Setter
@Getter
public class SectionResponse {
    private Long id;
    private Station upStation;
    private Station downStation;
    private Long distance;

    public SectionResponse(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionResponse that = (SectionResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
