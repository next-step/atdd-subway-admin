package nextstep.subway.line.service;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line_station.domain.LineStation;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 하행종점역입니다."));
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 상행종점역입니다."));

        LineStation lineStation = new LineStation(upStation, downStation, lineRequest.getDistance());
        Line persistLine = lineRepository.save(lineRequest.toLine(lineStation));

        return LineResponse.from(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lineList = lineRepository.findAll();

        return lineList.stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    public LineResponse findLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 노선입니다."));
        return LineResponse.from(line);
    }

    @Transactional
    public void modifyLine(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));

        line.update(lineRequest);
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

}
