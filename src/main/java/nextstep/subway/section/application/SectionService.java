package nextstep.subway.section.application;

import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

	private final StationRepository stationRepository;

	private final LineRepository lineRepository;

	public SectionService(StationRepository stationRepository, LineRepository lineRepository) {
		this.stationRepository = stationRepository;
		this.lineRepository = lineRepository;
	}

	// 비즈니스 로직은 최대한 도메인 객체에 구현하고 서비스는 데이터베이스에서 엔티티를 불러와 도메인을 호출하는 형태로 바꿔보면
//	Line line = lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + lineId));
//	LIneStation lineStation = new LineStation();
//		line.addSection(lineStation);

	@Transactional
	public void addSection(Long lineId, SectionRequest sectionRequest) {
//		Line line = lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + lineId));
//		List<LineStation> lineStations = line.getLineStations();
//
//		Map<StationType, LineStation> typeLineStationMap = lineStations.stream()
//				.filter(lineStation -> !lineStation.getStationType().equals(StationType.NONE))
//				.collect(Collectors.toMap(LineStation::getStationType, lineStation -> lineStation));
//
//		SectionAddCondition sectionAddCondition = checkAddSectionStatus(sectionRequest, typeLineStationMap);
//		if (sectionAddCondition.isBetweenAdd()) {
//			if (line.checkDistanceValidate(sectionRequest.getDistance())) {
//				throw new DistanceException();
//			}
//			updateBetweenStation(sectionAddCondition, sectionRequest, line);
//			return;
//		}
//		updateNewTerminal(sectionAddCondition, typeLineStationMap, line, sectionRequest);
	}

//	private SectionAddCondition checkAddSectionStatus(SectionRequest sectionRequest, Map<StationType, LineStation> typeLineStationMap) {
//		boolean isOldUpStation = typeLineStationMap.get(StationType.UP_STATION).getStationId() == sectionRequest.getUpStationId();
//		boolean isOldDownStation = typeLineStationMap.get(StationType.DOWN_STATION).getStationId() == sectionRequest.getDownStationId();
//
//		if (!isOldUpStation && !isOldDownStation) {
//			throw new NotIncludeLineBothStationException();
//		}
//		if (isOldUpStation && isOldDownStation) {
//			throw new AlreadyExistsStationException();
//		}
//
//		boolean isNewUpStation = typeLineStationMap.get(StationType.UP_STATION).getStationId() == sectionRequest.getDownStationId();
//		boolean isNewDownStation = typeLineStationMap.get(StationType.DOWN_STATION).getStationId() == sectionRequest.getUpStationId();
//
//		return new SectionAddCondition(isOldUpStation, isOldDownStation, isNewUpStation, isNewDownStation);
//	}
//
//	private void updateBetweenStation(SectionAddCondition sectionAddCondition, SectionRequest sectionRequest, Line line) {
//		Station station;
//		if (sectionAddCondition.isOldUpStation()) {
//			station = stationRepository.findById(sectionRequest.getDownStationId()).get();
//		}
//		station = stationRepository.findById(sectionRequest.getUpStationId()).get();
//		lineStationRepository.save(new LineStation(line, station, StationType.NONE));
//	}
//
//	private void updateNewTerminal(SectionAddCondition sectionAddCondition, Map<StationType, LineStation> typeLineStationMap, Line line, SectionRequest sectionRequest) {
//		if (sectionAddCondition.isNewUpStation()) {
//			updateLineStation(typeLineStationMap.get(StationType.UP_STATION), line, sectionRequest.getUpStationId(), StationType.UP_STATION);
//		}
//		updateLineStation(typeLineStationMap.get(StationType.DOWN_STATION), line, sectionRequest.getDownStationId(), StationType.DOWN_STATION);
//		line.addDistance(sectionRequest.getDistance());
//		lineRepository.save(line);
//	}
//
//	private void updateLineStation(LineStation oldLineStation, Line line, Long newStationId, StationType stationType) {
//		lineStationRepository.save(new LineStation(line, stationRepository.findById(newStationId).get(), stationType));
//		oldLineStation.updateNoneStationType();
//		lineStationRepository.save(oldLineStation);
//	}

	public void new_addSection(Long lineId, SectionRequest sectionRequest) {


	}
}
