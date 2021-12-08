package nextstep.subway.line.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.AppException;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {

	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		validateDuplication(request.getName());
		Line persistLine = lineRepository.save(request.toLine());
		return LineResponse.of(persistLine);
	}

	private void validateDuplication(String name) {
		if (lineRepository.existsByName(name)) {
			throw new AppException(ErrorCode.DUPLICATE_INPUT, name + "은 중복입니다");
		}
	}

	@Transactional(readOnly = true)
	public List<LineResponse> getLines() {
		return LineResponse.ofList(lineRepository.findAll());
	}

	@Transactional(readOnly = true)
	public LineResponse getLineById(Long id) {
		Line line = getById(id);
		return LineResponse.of(line);
	}

	public LineResponse modify(Long id, LineUpdateRequest lineRequest) {
		Line line = getById(id);
		line.update(lineRequest.toLine());
		return LineResponse.of(line);
	}

	private Line getById(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(() -> new AppException(ErrorCode.WRONG_INPUT, id + "는 존재하지 않습니다"));
	}

	public void deleteLineById(Long id) {
		Line line = getById(id);
		lineRepository.delete(line);
	}

	public LineResponse updateSections(Long id, SectionRequest sectionRequest) {
		Line line = getById(id);
		Station upStation = getStationById(sectionRequest.getUpStationId());
		Station downStation = getStationById(sectionRequest.getDownStationId());
		Section newSection = sectionRequest.toSection(line, upStation, downStation);
		line.updateSections(newSection);
		return LineResponse.of(line);
	}

	public Station getStationById(Long stationId) {
		return stationRepository.findById(stationId)
			.orElseThrow(() -> new AppException(ErrorCode.WRONG_INPUT, stationId + "는 존재하지 않습니다"));
	}

	public void removeSectionByStationId(Long lineId, Long stationId) {
		Line line = getById(lineId);
		Station removeStation = getStationById(stationId);
		line.removeSection(removeStation);
	}
}


