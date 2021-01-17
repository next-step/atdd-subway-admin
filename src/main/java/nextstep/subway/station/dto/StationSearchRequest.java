package nextstep.subway.station.dto;

import java.util.Arrays;
import java.util.List;

public class StationSearchRequest {

	private List<Long> ids;

	public StationSearchRequest() {
	}

	public StationSearchRequest(Long... ids) {
		this.ids = Arrays.asList(ids);
	}

	public List<Long> getIds() {
		return ids;
	}
}
