package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.common.exception.ResourceAlreadyExistException;
import nextstep.subway.common.exception.ResourceNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional(readOnly = true)
public class LineService {
	public static final String EXCEPTION_MESSAGE_LINE_ALREADY_EXIST = "이미 존재하는 노선입니다.";
	public static final String EXCEPTION_MESSAGE_NOT_FOUND_LINE = "존재하지 않는 노선입니다.";
	public static final String EXCEPTION_MESSAGE_NOT_FOUND_STATION = "존재하지 않는 지하철 역입니다.";

	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest request) {
		validateAlreadyExist(request);
		Station upStation = getStation(request.getUpStationId());
		Station downStation = getStation(request.getDownStationId());
		Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
		return LineResponse.of(persistLine);
	}

	private Station getStation(Long id) {
		return stationRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(EXCEPTION_MESSAGE_NOT_FOUND_STATION));
	}

	public LineResponses findLineList() {
		List<Line> lines = lineRepository.findAll();
		return LineResponses.of(lines);
	}

	public LineResponse findLine(Long id) {
		Optional<Line> line = lineRepository.findById(id);
		return line.map(LineResponse::of)
			.orElseThrow(() -> new ResourceNotFoundException(EXCEPTION_MESSAGE_NOT_FOUND_LINE));
	}

	private void validateAlreadyExist(LineRequest request) {
		if (lineRepository.existsByName(request.getName())) {
			throw new ResourceAlreadyExistException(EXCEPTION_MESSAGE_LINE_ALREADY_EXIST);
		}
	}

	@Transactional
	public void updateLine(Long id, LineRequest request) {
		Line line = lineRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(EXCEPTION_MESSAGE_NOT_FOUND_LINE));
		line.update(new Line(request.getName(), request.getColor(), line.getSections()));
	}

	@Transactional
	public void deleteLine(Long id) {
		try {
			lineRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(EXCEPTION_MESSAGE_NOT_FOUND_LINE, e);
		}
	}
}
