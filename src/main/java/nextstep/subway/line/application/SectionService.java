package nextstep.subway.line.application;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.NotFoundStationByIdException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {

    private static final String NOT_FOUND_UP_STATION_ERROR_MESSAGE = "상행역의 정보를 찾지 못하였습니다.";
    private static final String NOT_FOUND_DOWN_STATION_ERROR_MESSAGE = "하행역의 정보를 찾지 못하였습니다.";

    private final StationRepository stationRepository;

    public SectionService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    protected Section getSectionOrElseThrow(SectionRequest sectionRequest) {
        final int distance = sectionRequest.getDistance();
        final Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(() -> {
                    throw new NotFoundStationByIdException(NOT_FOUND_UP_STATION_ERROR_MESSAGE);
                });
        final Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(() -> {
                    throw new NotFoundStationByIdException(NOT_FOUND_DOWN_STATION_ERROR_MESSAGE);
                });
        return Section.of(distance, upStation, downStation);
    }
}
