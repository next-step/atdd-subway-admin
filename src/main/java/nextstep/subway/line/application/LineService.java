package nextstep.subway.line.application;

import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

	private LineRepository lineRepository;

	private StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public List<LineResponse> listLine() {
		List<Line> findLines = lineRepository.findAll();
		return findLines.stream().map(LineResponse::of).collect(Collectors.toList());
	}

	public LineResponse findLineById(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + id));
		return LineResponse.of(line);
	}

	@Transactional
	public void update(LineRequest lineRequest) {
		Line line = lineRepository.findById(lineRequest.getId()).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + lineRequest.getId()));
		line.update(lineRequest.toLine());
		lineRepository.save(line);
	}

	@Transactional
	public void delete(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + id));
		lineRepository.delete(line);
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest) {
		Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(() -> new IllegalArgumentException("해당 역이 없습니다 id=" + lineRequest.getUpStationId()));
		Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(() -> new IllegalArgumentException("해당 역이 없습니다 id=" + lineRequest.getDownStationId()));

		Line line = new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance());
		lineRepository.save(line);
		return LineResponse.of(line);
	}

	@Transactional
	public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
		Line line = lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("해당 역이 없습니다 id=" + lineId));
		Section newSection = toSection(line, sectionRequest);
		line.addSection(newSection, line);
		return LineResponse.of(lineRepository.save(line));
	}

	private Section toSection(Line line, SectionRequest sectionRequest) {
		Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(() -> new IllegalArgumentException("해당 역이 없습니다 id=" + sectionRequest.getUpStationId()));
		Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(() -> new IllegalArgumentException("해당 역이 없습니다 id=" + sectionRequest.getDownStationId()));
		return new Section(line, upStation, downStation, sectionRequest.getDistance(), upStation);
	}

	@Transactional
	public void removeSectionByStationId(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + lineId));
		Optional<Section> sectionOptional = line.getLineSections().stream().filter(sec -> (sec.getUpStation() == null && sec.getMainStation().getId() == stationId) ||
				(sec.getDownStation() == null && sec.getMainStation().getId() == stationId)).findAny();

		validate(sectionOptional, line);

		//todo 아래 코드들 도메인 로직으로 옮기기
		Section section = sectionOptional.orElseThrow(() -> new IllegalArgumentException("해당 구간이 없습니다 id=" + stationId));
		if(section.isTerminal()){
			line.removeTerminal(section);

		}
		//중간역이 삭제되는 경우

		lineRepository.save(line);
	}

	private void validate(Optional<Section> section, Line line) {
		if(!section.isPresent()){
			throw new IllegalArgumentException("등록되어있지 않은 역입니다.");
		}

		if(line.isImpossibleRemoveSection()){
			throw new RuntimeException("구간이 1개인 경우, 삭제할 수 없습니다");
		}
	}
}
