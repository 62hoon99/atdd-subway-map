package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.client.StationClient;
import nextstep.subway.acceptance.util.GivenUtils;
import nextstep.subway.acceptance.util.HttpStatusValidator;
import nextstep.subway.acceptance.util.JsonResponseConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;

import static nextstep.subway.acceptance.util.GivenUtils.강남역_이름;
import static nextstep.subway.acceptance.util.GivenUtils.이호선역_이름들;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @Autowired
    StationClient stationClient;

    @Autowired
    HttpStatusValidator statusValidator;

    @Autowired
    JsonResponseConverter responseConverter;

    @Autowired
    GivenUtils givenUtils;

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    @DirtiesContext
    void createStation() {
        // given

        // when
        ExtractableResponse<Response> response = givenUtils.강남역_생성();

        // then
        statusValidator.validateCreated(response);
        assertThat(responseConverter.convertToNames(stationClient.fetchStations()))
                .containsExactly(강남역_이름);
    }

    /**
     * When 지하철역을 중복 생성하면
     * Then 지하철역 목록 조회 시 생성한 역이 중복되지 않고 찾을 수 있다
     */
    @DisplayName("지하철역을 중복 생성한다.")
    @Test
    @DirtiesContext
    void createStationTwice() {
        // given
        int expectedSize = 1;

        // when
        ExtractableResponse<Response> firstCreationResponse = givenUtils.강남역_생성();

        // then
        statusValidator.validateCreated(firstCreationResponse);

        // when
        ExtractableResponse<Response> duplicatedResponse = givenUtils.강남역_생성();

        // then
        statusValidator.validateCreated(duplicatedResponse);
        assertThat(responseConverter.convertToNames(stationClient.fetchStations()))
                .hasSize(expectedSize)
                .containsExactly(강남역_이름);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    @DirtiesContext
    void getStations() {
        // given
        stationClient.createStations(이호선역_이름들);

        // when
        ExtractableResponse<Response> response = stationClient.fetchStations();

        // then
        statusValidator.validateOk(response);
        assertThat(responseConverter.convertToNames(response))
                .hasSize(이호선역_이름들.size())
                .containsExactly(이호선역_이름들.toArray(String[]::new));
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    @DirtiesContext
    void deleteStation() {
        // given
        Long stationId = responseConverter.convertToId(givenUtils.강남역_생성());

        // when
        ExtractableResponse<Response> response = stationClient.deleteStation(stationId);

        // then
        statusValidator.validateNoContent(response);
        assertThat(responseConverter.convertToNames(stationClient.fetchStations()))
                .doesNotContain(강남역_이름);
    }

    /**
     * When 존재하지 않는 지하철 역을 삭제하면
     * Then 해당 지하철 역 정보는 삭제된다
     */
    @DisplayName("존재하지 않는 지하철 역을 제거한다.")
    @Test
    @DirtiesContext
    void deleteNonExistentStation() {
        // given
        Long notExistsStationId = 1L;

        // when
        ExtractableResponse<Response> response = stationClient.deleteStation(notExistsStationId);

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(EmptyResultDataAccessException.class.getName());
    }

}