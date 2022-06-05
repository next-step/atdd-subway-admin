package nextstep.subway.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import nextstep.subway.domain.Section;

public class SectionResponse {
    private Long id;

    private LineResponse line;

    private StationResponse upStation;

    private StationResponse downStation;

    private Long distance;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    public SectionResponse(final Long id,
                           final LineResponse line,
                           final StationResponse upStation,
                           final StationResponse downStation,
                           final Long distance,
                           final LocalDateTime createdDate,
                           final LocalDateTime modifiedDate) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static SectionResponse of(final Section section) {
        return new SectionResponse(
                section.getId(),
                LineResponse.of(section.getLine()),
                StationResponse.of(section.getUpStation()),
                StationResponse.of(section.getDownStation()),
                section.getDistance(),
                section.getCreatedDate(),
                section.getModifiedDate());
    }

    @Override
    public String toString() {
        return "SectionResponse{" +
                "id=" + id +
                ", line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
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
                && Objects.equals(line, that.line)
                && Objects.equals(upStation, that.upStation)
                && Objects.equals(downStation, that.downStation)
                && Objects.equals(distance, that.distance)
                && Objects.equals(createdDate, that.createdDate)
                && Objects.equals(modifiedDate, that.modifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance, createdDate, modifiedDate);
    }
}
