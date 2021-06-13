package nextstep.subway.station.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.NotFoundData;
import nextstep.subway.station.domain.Station;
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

	@Transactional
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

	private Station findStationByIdFromRepository(Long id) {
		return stationRepository.findById(id).get();
	}

	@Transactional
	public void deleteStationById(Long id) {
		stationRepository.deleteById(id);
	}

	@Transactional
	public void updateStation(Long id, StationRequest stationRequest) {
		Station sourceStation = findStationByIdFromRepository(id);
		sourceStation.update(stationRequest.toStation());
	}
}
