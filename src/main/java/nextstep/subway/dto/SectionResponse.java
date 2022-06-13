package nextstep.subway.dto;

import java.util.Objects;

public class SectionResponse {
    private String lineName;

    private String upStationName;

    private String downStationName;

    private Long distance;

    public SectionResponse() {
    }

    public SectionResponse(final String lineName,
                           final String upStationName,
                           final String downStationName,
                           final Long distance) {
        this.lineName = lineName;
        this.upStationName = upStationName;
        this.downStationName = downStationName;
        this.distance = distance;
    }

    public String getLineName() {
        return lineName;
    }

    public String getUpStationName() {
        return upStationName;
    }

    public String getDownStationName() {
        return downStationName;
    }

    public Long getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "SectionResponse{" +
                "lineName='" + lineName + '\'' +
                ", upStationName='" + upStationName + '\'' +
                ", downStationName='" + downStationName + '\'' +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SectionResponse that = (SectionResponse) o;
        return Objects.equals(lineName, that.lineName)
                && Objects.equals(upStationName, that.upStationName)
                && Objects.equals(downStationName, that.downStationName)
                && Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineName, upStationName, downStationName, distance);
    }
}
