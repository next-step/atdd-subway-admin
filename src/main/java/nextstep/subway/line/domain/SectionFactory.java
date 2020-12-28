package nextstep.subway.line.domain;

public interface SectionFactory {

    Section create(final Long upStationId, final Long downStationId, final int distance);
}
