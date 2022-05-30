package nextstep.subway.application;

import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineStationService {
    private final LineStationRepository lineStationRepository;
    private final StationService stationService;

    public LineStationService(LineStationRepository lineStationRepository, StationService stationService) {
        this.lineStationRepository = lineStationRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineStation saveLineStation(Long distance, Station upStation, Station downStation) {
        return null;
    }

    public LineStation saveLineStation(LineRequest lineRequest) {
        Station upStation = stationService.getOrElseThrow(lineRequest.getUpStationId());
        Station downStation = stationService.getOrElseThrow(lineRequest.getDownStationId());
        return lineStationRepository.save(new LineStation(lineRequest.getDistance(), upStation, downStation));
    }
}
