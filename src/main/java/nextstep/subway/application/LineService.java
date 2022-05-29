package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineResponses;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(StationNotFoundException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(StationNotFoundException::new);
        Line persistLine = lineRepository.save(lineRequest.toLine());
        persistLine.setUpStation(upStation);
        persistLine.setDownStation(downStation);

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public LineResponses findAllLines() {
        List<Line> lines = lineRepository.findAll();
        List<LineResponse> list = lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
        return new LineResponses(list);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
