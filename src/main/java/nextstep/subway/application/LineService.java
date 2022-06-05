package nextstep.subway.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;

@Service
@Transactional(readOnly = true)
public class LineService {
	private LineRepository lineRepository;
	private StationService stationService;

	public LineService(
		LineRepository lineRepository,
		StationService stationService
	) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest) {
		Line persistLine = lineRepository.save(new Line(
			lineRequest.getName(),
			lineRequest.getColor()
		));

		if(lineRequest.getUpStationId() != null && lineRequest.getDownStationId() != null) {
			Station upStation = stationService.findById(lineRequest.getUpStationId());
			Station downStation = stationService.findById(lineRequest.getDownStationId());

			persistLine.setUpStation(upStation);
			persistLine.setDownStation(downStation);
		}

		return LineResponse.of(persistLine);
	}
}
