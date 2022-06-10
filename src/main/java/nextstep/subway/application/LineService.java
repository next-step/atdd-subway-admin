package nextstep.subway.application;

import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.line.CreateLineRequest;
import nextstep.subway.dto.line.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(
        LineRepository lineRepository,
        StationRepository stationRepository
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(CreateLineRequest createLineRequest) {
        Station upStation = stationRepository.findById(createLineRequest.getUpStationId())
            .orElseThrow(IllegalArgumentException::new);
        Station downStation = stationRepository.findById(createLineRequest.getDownStationId())
            .orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(lineRepository.save(createLineRequest.toEntity(upStation, downStation)));
    }

}
