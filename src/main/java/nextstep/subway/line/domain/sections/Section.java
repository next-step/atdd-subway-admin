package nextstep.subway.line.domain.sections;

import nextstep.subway.common.ValueObjectId;
import nextstep.subway.line.domain.exceptions.InvalidSectionException;
import nextstep.subway.line.domain.exceptions.MergeSectionFailException;

import javax.persistence.Entity;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends ValueObjectId {
    private static final Long MIN_DISTANCE = 0L;

    private Long upStationId;
    private Long downStationId;
    private Long distance;

    protected Section() {
    }

    Section(final Long id, final Long upStationId, final Long downStationId, final Long distance) {
        validate(upStationId, downStationId, distance);
        super.setId(id);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(final Long upStationId, final Long downStationId, final Long distance) {
        this(null, upStationId, downStationId, distance);
    }

    public boolean isSameUpWithThatDown(final Section thatSection) {
        return this.upStationId.equals(thatSection.downStationId);
    }

    public boolean isSameDownWithThatUp(final Section thatSection) {
        return this.downStationId.equals(thatSection.upStationId);
    }

    public boolean isHasBiggerDistance(final Section thatSection) {
        return this.distance >= thatSection.distance;
    }

    Section merge(final Section thatSection) {
        List<Long> thisStationIds = this.getStationIds();

        if (!thisStationIds.contains(thatSection.upStationId) && !thisStationIds.contains(thatSection.downStationId)) {
            throw new MergeSectionFailException(" 접점이 없는 Section을 병합할 수 없습니다.");
        }
        if (thisStationIds.containsAll(thatSection.getStationIds())) {
            throw new MergeSectionFailException(" 접점이 두개인 Section을 병합할 수 없습니다.");
        }
        if (this.isSameDownWithThatUp(thatSection)) {
            return new Section(upStationId, thatSection.downStationId, distance + thatSection.distance);
        }

        return new Section(thatSection.upStationId, downStationId, distance + thatSection.distance);
    }

    List<Long> getStationIds() {
        return Arrays.asList(upStationId, downStationId);
    }

    Long getUpStationId() {
        return this.upStationId;
    }

    Long getDownStationId() {
        return this.downStationId;
    }

    boolean isSameUpStation(final Section thatSection) {
        return this.upStationId.equals(thatSection.upStationId);
    }

    boolean isSameDownStation(final Section thatSection) {
        return this.downStationId.equals(thatSection.downStationId);
    }

    boolean isHasThisStation(final Long stationId) {
        return (this.upStationId.equals(stationId) || this.downStationId.equals(stationId));
    }

    Section createUpdatedUpStation(final Section section) {
        return new Section(section.downStationId, this.downStationId,this.distance - section.distance);
    }

    Section createUpdatedDownStation(final Section section) {
        return new Section(this.upStationId, section.upStationId, this.distance - section.distance);
    }

    boolean isUpStationBelongsTo(final List<Long> stationIds) {
        return stationIds.contains(this.upStationId);
    }

    boolean isDownStationBelongsTo(final List<Long> stationIds) {
        return stationIds.contains(this.downStationId);
    }

    private void validate(final Long upStationId, final Long downStationId, final Long distance) {
        validateStations(upStationId, downStationId);
        validateDistance(distance);
    }

    private void validateStations(final Long upStationId, final Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new InvalidSectionException("상행역과 하행역은 같은 역일 수 없습니다.");
        }
    }

    private void validateDistance(final Long distance) {
        if (distance.equals(MIN_DISTANCE)) {
            throw new InvalidSectionException("거리가 0인 구간은 생성할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Section section = (Section) o;
        return Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
