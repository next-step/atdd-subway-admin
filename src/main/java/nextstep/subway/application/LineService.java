package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.request.LineRequest;
import nextstep.subway.dto.response.LineResponse;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LineService {

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;

    public LineResponse createLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(
            StationNotFoundException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
            .orElseThrow(
                StationNotFoundException::new);

        Line savedLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(savedLine);
    }
}
