package nextstep.subway.station.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@Service
@Transactional
public class StationService {
	private static final String STATION_NOT_FOUND_MESSAGE = "id에 해당하는 Station을 찾을 수 없습니다.";
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
			.map(station -> StationResponse.of(station))
			.collect(Collectors.toList());
	}

	public void deleteStationById(Long id) {
		stationRepository.deleteById(id);
	}

	public Station getStation(Long upStationId) {
		return this.stationRepository.findById(upStationId)
			.orElseThrow(() -> new EntityNotFoundException(STATION_NOT_FOUND_MESSAGE));
	}
}
