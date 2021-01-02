package nextstep.subway.section;

import nextstep.subway.common.StationType;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStationRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SectionService {

	private final StationRepository stationRepository;

	private final LineRepository lineRepository;

	private final LineStationRepository lineStationRepository;

	public SectionService(LineStationRepository lineStationRepository, StationRepository stationRepository, LineRepository lineRepository) {
		this.lineStationRepository = lineStationRepository;
		this.stationRepository = stationRepository;
		this.lineRepository = lineRepository;
	}

	@Transactional
	public void addSection(Long lineId, SectionRequest sectionRequest) {
		Line line = lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + lineId));
		List<LineStation> lineStations = line.getLineStations();

		Map<StationType, LineStation> typeLineStationMap = lineStations.stream()
				.filter(lineStation -> !lineStation.getStationType().equals(StationType.NONE))
				.collect(Collectors.toMap(LineStation::getStationType, lineStation -> lineStation));

		SectionAddCondition sectionAddCondition = checkAddSectionStatus(sectionRequest, typeLineStationMap);
		if (sectionAddCondition.isBetweenAdd()) {
			if (line.checkDistanceValidate(sectionRequest.getDistance())) {
				throw new RuntimeException("상.하행역 사이에 추가되는 역의 길이는 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다");
			}
			updateBetweenStation(sectionAddCondition, sectionRequest, line);
			return;
		}
		updateNewTerminal(sectionAddCondition, typeLineStationMap, line, sectionRequest);
	}

	private SectionAddCondition checkAddSectionStatus(SectionRequest sectionRequest, Map<StationType, LineStation> typeLineStationMap) {
		boolean isOldUpStation = typeLineStationMap.get(StationType.UP_STATION).getStationId() == sectionRequest.getUpStationId();
		boolean isOldDownStation = typeLineStationMap.get(StationType.DOWN_STATION).getStationId() == sectionRequest.getDownStationId();

		if (!isOldUpStation && !isOldDownStation) {
			throw new RuntimeException("해당 노선에 상행역과 하행역 둘 중 하나의 역도 포함되어있지 않습니다");
		}
		if (isOldUpStation && isOldDownStation) {
			throw new RuntimeException("기등록된 상행역, 하행역 정보와 같습니다");
		}

		boolean isNewUpStation = typeLineStationMap.get(StationType.UP_STATION).getStationId() == sectionRequest.getDownStationId();
		boolean isNewDownStation = typeLineStationMap.get(StationType.DOWN_STATION).getStationId() == sectionRequest.getUpStationId();

		return new SectionAddCondition(isOldUpStation, isOldDownStation, isNewUpStation, isNewDownStation);
	}

	private void updateBetweenStation(SectionAddCondition sectionAddCondition, SectionRequest sectionRequest, Line line) {
		Station station;
		if (sectionAddCondition.isOldUpStation()) {
			station = stationRepository.findById(sectionRequest.getDownStationId()).get();
		}
		station = stationRepository.findById(sectionRequest.getUpStationId()).get();
		lineStationRepository.save(new LineStation(line, station, StationType.NONE));
	}

	private void updateNewTerminal(SectionAddCondition sectionAddCondition, Map<StationType, LineStation> typeLineStationMap, Line line, SectionRequest sectionRequest) {
		if (sectionAddCondition.isNewUpStation()) {
			updateLineStation(typeLineStationMap.get(StationType.UP_STATION), line, sectionRequest.getUpStationId(), StationType.UP_STATION);
		}
		updateLineStation(typeLineStationMap.get(StationType.DOWN_STATION), line, sectionRequest.getDownStationId(), StationType.DOWN_STATION);
		line.addDistance(sectionRequest.getDistance());
		lineRepository.save(line);
	}

	private void updateLineStation(LineStation oldLineStation, Line line, Long newStationId, StationType stationType) {
		lineStationRepository.save(new LineStation(line, stationRepository.findById(newStationId).get(), stationType));
		oldLineStation.updateNoneStationType();
		lineStationRepository.save(oldLineStation);
	}
}
