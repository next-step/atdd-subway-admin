package nextstep.subway.line.domain;

import nextstep.subway.common.ValueObjectId;
import nextstep.subway.line.domain.exceptions.InvalidSectionException;

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

    List<Long> getStationIds() {
        return Arrays.asList(upStationId, downStationId);
    }

    boolean isSameUpStation(final Section thatSection) {
        return this.upStationId.equals(thatSection.upStationId);
    }

    boolean isSameDownStation(final Section thatSection) {
        return this.downStationId.equals(thatSection.downStationId);
    }

    boolean isSameUpWithThatDown(final Section thatSection) {
        return this.upStationId.equals(thatSection.downStationId);
    }

    boolean isSameDownWithThatUp(final Section thatSection) {
        return this.downStationId.equals(thatSection.upStationId);
    }

    void updateUpStation(final Section section) {
        this.upStationId = section.downStationId;
        this.distance = this.distance - section.distance;
    }

    void updateDownStation(final Section section) {
        this.downStationId = section.upStationId;
        this.distance = this.distance - section.distance;
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
}
