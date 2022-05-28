package nextstep.subway.application;

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
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveStation(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).get();
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).get();
        Line persistLine = lineRepository.save(lineRequest.toLine());
        persistLine.setUpStation(upStation);
        persistLine.setDownStation(downStation);

        return LineResponse.of(persistLine);
    }
}
