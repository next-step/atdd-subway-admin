package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
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
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 호선입니다."));
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 호선입니다."));

        Line persistLine = lineRepository.save(lineRequest.toLine(downStation, upStation));
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
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 호선입니다."));
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
