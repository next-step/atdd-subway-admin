package nextstep.subway.application;

import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream().map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.getStation(lineRequest.getUpStationId());
        Station downStation = stationService.getStation(lineRequest.getDownStationId());

        return LineResponse.of(lineRepository.save(lineRequest.toLine(upStation, downStation)));
    }
}
