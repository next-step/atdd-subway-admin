package nextstep.subway.application.stations;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.stations.StationRequest;
import nextstep.subway.dto.stations.StationResponse;
import nextstep.subway.exception.StationNotFoundException;

@Service
public class StationService {
	private static final String STATION_NOT_FOUND_MESSAGE = "존재하지 않는 역입니다. | id : ";
	private final StationRepository stationRepository;

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

	@Transactional
	public void deleteStationById(Long id) {
		stationRepository.deleteById(id);
	}

	public Station findById(Long id) {
		return stationRepository.findById(id)
			.orElseThrow(() -> new StationNotFoundException(STATION_NOT_FOUND_MESSAGE + id));
	}

	public List<Station> findAllById(Long upStationId, Long downStationId) {
		return stationRepository.findAllById(Lists.asList(upStationId, downStationId, new Long[]{}));
	}
}
