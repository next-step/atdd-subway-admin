package nextstep.subway.line.application;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private static final String LINE_NOT_FOUND_MESSAGE = "원하시는 지하천 노선을 찾지 못했습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(NotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(NotFoundException::new);

        return LineResponse.of(lineRepository.save(request.toLine(upStation, downStation)));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> selectAllLines() {
        return LineResponse.ofList(lineRepository.findAll());
    }

    @Transactional(readOnly = true)
    public LineResponse selectLine(Long id) {
        return LineResponse.of(lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND_MESSAGE)));
    }

    public LineResponse modifyLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND_MESSAGE));
        line.update(request.toLine());
        return LineResponse.of(lineRepository.save(line));
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
