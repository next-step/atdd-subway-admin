package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.common.ErrorCode;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.LineException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationException;

@Service
@Transactional
public class LineService {

	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineService(LineRepository lineRepository,
		StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest request) {
		Station upStation = stationFindById(request.getUpStationId());
		Station downStation = stationFindById(request.getDownStationId());

		Line line = request.toLine();
		line.addSection(new Section(line, upStation, downStation, request.getDistance()));

		Line persistLine = lineRepository.save(line);

		return LineResponse.of(persistLine);
	}

	public List<LineResponse> findAll() {
		List<Line> lineList = lineRepository.findAll();
		return lineList
			.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse findById(Long id) {
		Line line = lineFindById(id);
		return LineResponse.of(line);
	}

	@Transactional
	public LineResponse updateLine(Long id, LineRequest lineRequest) {
		Line line = lineFindById(id);

		line.update(lineRequest.toLine());

		lineRepository.save(line);

		return LineResponse.of(line);
	}

	@Transactional
	public void deleteLine(Long id) {
		Line line = lineFindById(id);

		lineRepository.delete(line);
	}

	private Line lineFindById(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(() ->
				new LineException(ErrorCode.LINE_NULL_POINTER_ERROR)
			);
	}

	private Station stationFindById(Long id) {
		return stationRepository.findById(id)
			.orElseThrow(() ->
				new StationException(ErrorCode.STATION_NULL_POINTER_ERROR)
			);
	}

	public LineResponse addSection(Long id, SectionRequest sectionRequest) {
		Line line = lineFindById(id);
		Section section = reqToSection(line, sectionRequest);
		line.addSection(section);
		return LineResponse.of(line);
	}

	private Section reqToSection(Line line, SectionRequest sectionRequest) {
		Station upStation = stationFindById(sectionRequest.getUpStationId());
		Station downStation = stationFindById(sectionRequest.getDownStationId());
		return new Section(line, downStation, upStation, sectionRequest.getDistance());
	}

	@Transactional
	public void removeLineStation(Long lineId, Long stationId) {
		Line line = lineFindById(lineId);
		Station station = stationFindById(stationId);
		line.getSections().removeLineStation(station);
	}
}
