package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());

        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public LineResponse findLine(Long id) {
        return  LineResponse.of(lineRepository.getById(id));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("지하철 노선 정보가 존재하지 않습니다."));
        persistLine.update(lineRequest);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.delete(findLineById(id));
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("지하철 노선 정보가 존재하지 않습니다."));
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("지하철역 정보가 존재하지 않습니다."));
    }
}
