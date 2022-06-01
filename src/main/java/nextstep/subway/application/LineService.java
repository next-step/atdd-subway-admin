package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineCreateResponse;
import nextstep.subway.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineCreateResponse saveLine(LineCreateRequest lineCreateRequest) {
        Station upStation = stationRepository.findById(lineCreateRequest.getUpStationId())
                .orElseThrow(() -> new NotFoundException("상행역을 찾을 수 없습니다."));

        Station downStation = stationRepository.findById(lineCreateRequest.getUpStationId())
                .orElseThrow(() -> new NotFoundException("하행역을 찾을 수 없습니다."));

        Line line = lineRepository.save(new Line(lineCreateRequest.getName(), lineCreateRequest.getColor(), upStation, downStation, lineCreateRequest.getDistance()));

        return new LineCreateResponse(line.getId(), line.getName());
    }
}
