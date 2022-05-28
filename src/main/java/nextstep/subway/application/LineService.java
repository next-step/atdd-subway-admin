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
import nextstep.subway.dto.StationResponse;
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
        Optional<Station> optionalDownStation = stationRepository.findById(lineRequest.getDownStationId());
        Optional<Station> optionalUpStation = stationRepository.findById(lineRequest.getUpStationId());

        if (!optionalDownStation.isPresent() || !optionalUpStation.isPresent()) {
            throw new IllegalArgumentException("종점역이 존재하지 않습니다.");
        }

        Line persistLine = lineRepository.save(lineRequest.toLine(optionalDownStation.get(), optionalUpStation.get()));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lineList = lineRepository.findAll();

        return lineList.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

}
