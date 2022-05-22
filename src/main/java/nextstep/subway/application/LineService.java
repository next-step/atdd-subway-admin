package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.NewLineRequest;
import nextstep.subway.dto.NewLineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository repository;
    private final StationService stationService;

    public LineService(LineRepository repository, StationService stationService) {
        this.repository = repository;
        this.stationService = stationService;
    }

    public NewLineResponse saveLine(NewLineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getUpStationId());
        final Line savedLine = repository.save(new Line(request.getName(), request.getColor(), upStation, downStation,
                                                        request.getDistance()));
        return new NewLineResponse(savedLine);
    }
}
