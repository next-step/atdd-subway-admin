package nextstep.subway.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        return LineResponse.of(findLineById(id));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line persistLine = findLineById(id);
        persistLine.change(lineRequest);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.delete(findLineById(id));
    }

    private Line findLineById(Long id) {
        Optional<Line> optionalLine = lineRepository.findById(id);
        return optionalLine
                .orElseThrow(() -> new NotFoundException("라인 정보가 존재하지 않습니다."));
    }

    private Station findStationById(Long id) {
        Optional<Station> optionalStation = stationRepository.findById(id);
        return optionalStation
                .orElseThrow(() -> new NotFoundException("역 정보가 존재하지 않습니다."));
    }
}
