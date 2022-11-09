package nextstep.subway.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.StationNotFoundException;

@Service
@Transactional(readOnly = true)
public class LineService {

	private final StationRepository stationRepository;
	private final LineRepository lineRepository;

	public LineService(StationRepository stationRepository, LineRepository lineRepository) {
		this.stationRepository = stationRepository;
		this.lineRepository = lineRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest) {
		Line line = getLine(lineRequest);

		line = lineRepository.save(line);

		return LineResponse.of(line);
	}

	private Line getLine(LineRequest lineRequest) {
		Station upStation = getStation(lineRequest.getUpStationId());
		Station downStation = getStation(lineRequest.getDownStationId());

		return lineRequest.toLine(upStation, downStation);
	}

	private Station getStation(Long upStationId) {
		return stationRepository.findById(upStationId).orElseThrow(() -> new StationNotFoundException(upStationId));
	}

	public List<LineResponse> findAllLines() {
		return LineResponse.ofList(lineRepository.findAll());
	}
}
