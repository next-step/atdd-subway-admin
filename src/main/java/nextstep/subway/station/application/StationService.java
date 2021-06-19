package nextstep.subway.station.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationGroup;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@Service
@Transactional
public class StationService {
	private StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public StationResponse saveStation(StationRequest stationRequest) {
		Station persistStation = stationRepository.save(stationRequest.toStation());
		return StationResponse.of(persistStation);
	}

	@Transactional(readOnly = true)
	public List<StationResponse> findAllStations() {
		List<Station> stations = stationRepository.findAll();

		return stations.stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public StationResponse findStationById(Long id) {
		return StationResponse.of(findStationByIdFromRepository(id));
	}

	@Transactional(readOnly = true)
	public Station findStationByIdFromRepository(Long id) {
		return Optional.ofNullable(stationRepository.findById(id)).get()
			.orElseThrow(new NotFoundException("지하철 역을 찾을 수 없습니다. id :" + id));
	}

	public void deleteStationById(Long id) {
		stationRepository.deleteById(id);
	}

	public void updateStation(Long id, StationRequest stationRequest) {
		Station sourceStation = findStationByIdFromRepository(id);
		sourceStation.update(stationRequest.toStation());
	}

	public StationGroup addUpStationAndDownStation(List<Long> stationIdsToAdd) {
		return stationIdsToAdd.stream()
			.map(this::findStationByIdFromRepository)
			.collect(Collectors.collectingAndThen(Collectors.toList(), StationGroup::new));
	}
}
