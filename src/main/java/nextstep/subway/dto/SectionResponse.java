package nextstep.subway.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import nextstep.subway.domain.Section;

public class SectionResponse {
    private Long id;

    private String lineName;

    private String upStationName;

    private String downStationName;

    private Long distance;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    public SectionResponse() {
    }

    public SectionResponse(final Long id,
                           final String lineName,
                           final String upStationName,
                           final String downStationName,
                           final Long distance,
                           final LocalDateTime createdDate,
                           final LocalDateTime modifiedDate) {
        this.id = id;
        this.lineName = lineName;
        this.upStationName = upStationName;
        this.downStationName = downStationName;
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static SectionResponse of(final Section section) {
        return new SectionResponse(
                section.getId(),
                section.getLine().getName(),
                section.getUpStation().getName(),
                section.getDownStation().getName(),
                section.getDistance(),
                section.getCreatedDate(),
                section.getModifiedDate());
    }

    public Long getId() {
        return id;
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public String toString() {
        return "SectionResponse{" +
                "id=" + id +
                ", lineName='" + lineName + '\'' +
                ", upStationName='" + upStationName + '\'' +
                ", downStationName='" + downStationName + '\'' +
                ", distance=" + distance +
                ", createdDate=" + createdDate +
                ", modifiedDate=" + modifiedDate +
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
        return Objects.equals(id, that.id)
                && Objects.equals(lineName, that.lineName)
                && Objects.equals(upStationName, that.upStationName)
                && Objects.equals(downStationName, that.downStationName)
                && Objects.equals(distance, that.distance)
                && Objects.equals(createdDate, that.createdDate)
                && Objects.equals(modifiedDate, that.modifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineName, upStationName, downStationName, distance, createdDate, modifiedDate);
    }
}
