package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.constant.ErrorCode;
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

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public LineService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.상행종착역은_비어있을_수_없음.getErrorMessage()));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.하행종착역은_비어있을_수_없음.getErrorMessage()));

        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public LineResponse findLineById(Long id) {
        Line findLine = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.해당하는_노선_없음.getErrorMessage()));
        return LineResponse.of(findLine);
    }
}
