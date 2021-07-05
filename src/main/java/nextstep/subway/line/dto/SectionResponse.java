package nextstep.subway.line.dto;

import lombok.Getter;
import lombok.Setter;
import nextstep.subway.station.domain.Station;

import java.util.Objects;

@Setter
@Getter
public class SectionResponse {
    private Station upStation;
    private Station downStation;
    private Long distance;

    public static SectionResponse of(Station upStation, Station downStation, Long distance) {
        SectionResponse response = new SectionResponse();
        response.setUpStation(upStation);
        response.setDownStation(downStation);
        response.setDistance(distance);
        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionResponse that = (SectionResponse) o;
        return Objects.equals(upStation, that.upStation) && Objects.equals(downStation, that.downStation) && Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, distance);
    }
}
