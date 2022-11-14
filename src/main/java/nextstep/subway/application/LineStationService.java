package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineStationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineStationService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineStationService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse addLineStation(Long lineId, LineStationRequest lineStationRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new RuntimeException("지하철 노선이 존재하지 않습니다."));
        Station upStation = stationRepository.findById(lineStationRequest.getUpStationId()).get(); // TODO: optional 사용 어떻게 하는게 좋은 코드인지?
        Station downStation = stationRepository.findById(lineStationRequest.getDownStationId()).get();

        line.addLineStation(lineStationRequest.toLineStation(upStation, downStation));
        lineRepository.save(line);
        return LineResponse.of(line);
    }
}
