package nextstep.subway.application;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class SectionMapper {
    public static final String NOT_FOUND_LINE = "해당 지하철역을 찾을 수 없습니다";

    private final StationRepository stationRepository;

    public SectionMapper(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Section of(Long upStationId, Long downStationId, int distance) {
        Station upStation = stationRepository.findById(upStationId)
                .orElseThrow(() -> new NoSuchElementException(NOT_FOUND_LINE));
        Station downStation = stationRepository.findById(downStationId)
                .orElseThrow(() -> new NoSuchElementException(NOT_FOUND_LINE));

        return new Section(distance, upStation, downStation);
    }

    public Section from(LineRequest request) {
        return of(request.getUpStationId(), request.getDownStationId(), request.getDistance());
    }

    public Section from(SectionRequest request) {
        return of(request.getUpStationId(), request.getDownStationId(), request.getDistance());
    }
}
