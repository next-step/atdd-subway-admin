package nextstep.subway.station.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationService {

	private final StationRepository stationRepository;

	@Transactional
	public StationResponse saveStation(StationRequest stationRequest) {
		Station persistStation = stationRepository.save(stationRequest.toStation());
		return StationResponse.of(persistStation);
	}

	public List<StationResponse> findAllStations() {
		List<Station> stations = stationRepository.findAll();

		return stations.stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());
	}

	public void deleteStationById(Long id) {
		stationRepository.deleteById(id);
	}
}
