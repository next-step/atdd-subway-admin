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
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new IllegalStateException("해당 지하철 역이 존재하지 않습니다."));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new IllegalStateException("해당 지하철 역이 존재하지 않습니다."));
        Line save = lineRepository.save(Line
                .of(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance()));
        return LineResponse.of(save);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }
}
