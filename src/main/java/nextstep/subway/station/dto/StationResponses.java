package nextstep.subway.station.dto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import nextstep.subway.station.domain.Station;

public class StationResponses {
    private List<StationResponse> responses;

    public StationResponses(List<StationResponse> responses) {
        this.responses = responses;
    }

    protected StationResponses() {
    }

    public static StationResponses from(List<Station> stations) {
        List<StationResponse> responses = stations
            .stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());

        return new StationResponses(responses);
    }

    public List<StationResponse> getResponses() {
        return responses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StationResponses)) {
            return false;
        }
        StationResponses that = (StationResponses)o;
        return Objects.equals(responses, that.responses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responses);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StationResponses{");
        sb.append("responses=").append(responses);
        sb.append('}');
        return sb.toString();
    }
}
