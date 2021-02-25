package org.poolc.api.activity;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.poolc.api.AcceptanceTest;
import org.poolc.api.activity.dto.*;
import org.poolc.api.auth.dto.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.poolc.api.auth.AuthAcceptanceTest.loginRequest;

@ActiveProfiles("activityTest")
public class ActivityAcceptanceTest extends AcceptanceTest {

    @Test
    public void 액티비티전체조회() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = getActivitiesRequest(accessToken);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getList("data")).hasSize(4);
    }

    @Test
    public void 액티비티하나조회() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = getActivityRequest(accessToken, 6l);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void 없는액티비티조회시에러() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = getActivityRequest(accessToken, 655l);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    public void 액티비티등록() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        LocalDate localDate = LocalDate.now();
        List<String> tags = new ArrayList<>();
        tags.add("꿀잼");
        tags.add("깨꿀잼");
        ExtractableResponse<Response> response = createActivityRequest(accessToken, "김성하의 재미있는 sql세미나", "이거 들으면 취업가능", localDate, true, "3시간", 200l, tags, 200l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> response2 = getActivitiesRequest(accessToken);
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response2.body().jsonPath().getList("data")).hasSize(5);

    }

    @Test
    public void 본인이액티비티수정() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        LocalDate localDate = LocalDate.now();
        List<String> tags = new ArrayList<>();
        tags.add("꿀잼");
        tags.add("깨꿀잼");
        tags.add("한시간만 들어도 알고리즘 정복가능");
        ExtractableResponse<Response> response = updateActivityRequest(accessToken, 1l, "김성하의 재미있는 sql세미나", "이거 들으면 취업가능", localDate, true, "3시간", 200l, tags, 200l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> response2 = getActivitiesRequest(accessToken);
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response2.body().jsonPath().getList("data")).hasSize(4);

    }

    @Test
    public void 없는액티비티수정() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        LocalDate localDate = LocalDate.now();
        List<String> tags = new ArrayList<>();
        tags.add("꿀잼");
        tags.add("깨꿀잼");
        tags.add("한시간만 들어도 알고리즘 정복가능");
        ExtractableResponse<Response> response = updateActivityRequest(accessToken, 432l, "김성하의 재미있는 sql세미나", "이거 들으면 취업가능", localDate, true, "3시간", 200l, tags, 200l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());


    }

    @Test
    public void 임원진이액티비티수정() {
        String accessToken = loginRequest("MEMBER_ID3", "MEMBER_PASSWORD3")
                .as(AuthResponse.class)
                .getAccessToken();

        LocalDate localDate = LocalDate.now();
        List<String> tags = new ArrayList<>();
        tags.add("꿀잼");
        tags.add("깨꿀잼");
        tags.add("한시간만 들어도 알고리즘 정복가능");
        ExtractableResponse<Response> response = updateActivityRequest(accessToken, 1l, "김성하의 재미있는 sql세미나", "이거 들으면 취업가능", localDate, true, "3시간", 200l, tags, 200l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> response2 = getActivitiesRequest(accessToken);
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response2.body().jsonPath().getList("data")).hasSize(4);

    }

    @Test
    public void 호스트나임원진이아닌사람이액티비티수정() {
        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();

        LocalDate localDate = LocalDate.now();
        List<String> tags = new ArrayList<>();
        tags.add("꿀잼");
        tags.add("깨꿀잼");
        tags.add("한시간만 들어도 알고리즘 정복가능");

        ExtractableResponse<Response> response = updateActivityRequest(accessToken, 1l, "김성하의 재미있는 sql세미나", "이거 들으면 취업가능", localDate, true, "3시간", 200l, tags, 200l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }

    @Test
    public void 호스트가액티비티삭제() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = deleteActivityRequest(accessToken, 3l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> response2 = getActivitiesRequest(accessToken);

        assertThat(response2.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response2.body().jsonPath().getList("data")).hasSize(3);

    }

    @Test
    public void 없는액티비티삭제() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = deleteActivityRequest(accessToken, 432l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());


    }

    @Test
    public void 임원진이액티비티삭제() {
        String accessToken = loginRequest("MEMBER_ID3", "MEMBER_PASSWORD3")
                .as(AuthResponse.class)
                .getAccessToken();


        ExtractableResponse<Response> response = deleteActivityRequest(accessToken, 1l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    public void 호스트나임원진이아닌사람이액티비티삭제() {
        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();

        LocalDate localDate = LocalDate.now();
        List<String> tags = new ArrayList<>();
        tags.add("꿀잼");
        tags.add("깨꿀잼");
        tags.add("한시간만 들어도 알고리즘 정복가능");

        ExtractableResponse<Response> response = deleteActivityRequest(accessToken, 1l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }

    @Test
    public void 호스트가회차정보입력() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = createSessionRequest(accessToken, 1l, 1l, "김성하의c++세미나 1회차", LocalDate.now());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    public void 없는액티비티정보입력() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = createSessionRequest(accessToken, 10l, 1l, "김성하의c++세미나 1회차", LocalDate.now());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    public void 호스트가아닌사람이액티비티정보입력() {
        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = createSessionRequest(accessToken, 1l, 1l, "김성하의c++세미나 1회차", LocalDate.now());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }

    @Test
    public void 세션정보모두조회() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response2 = createSessionRequest(accessToken, 1l, 1l, "김성하의c++세미나 1회차", LocalDate.now());
        ExtractableResponse<Response> response3 = createSessionRequest(accessToken, 1l, 2l, "김성하의c++세미나 1회차", LocalDate.now());

        ExtractableResponse<Response> response = getSessionsRequest(accessToken, 1l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getList("data").size()).isEqualTo(2);

    }

    @Test
    public void 호스트가아닌사람이세션정보조조회() {
        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();


        ExtractableResponse<Response> response = getSessionsRequest(accessToken, 1l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }

    @Test
    public void 없는활동세션정보조조회() {
        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();


        ExtractableResponse<Response> response = getSessionsRequest(accessToken, 4l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    public void 회차정보수정() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response2 = createSessionRequest(accessToken, 1l, 1l, "김성하의c++세미나 1회차", LocalDate.now());
        ExtractableResponse<Response> response3 = createSessionRequest(accessToken, 1l, 2l, "김성하의c++세미나 1회차", LocalDate.now());
        ExtractableResponse<Response> response4 = getSessionsRequest(accessToken, 1l);

        Long sessionID = response4.body().jsonPath().getLong("data[0].id");
        String newDescription = "사실 정윤석이함";
        LocalDate date = LocalDate.of(2011, 11, 11);

        ExtractableResponse<Response> response = updateSessionRequest(accessToken, sessionID, date, newDescription);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> response5 = getSessionsRequest(accessToken, 1l);
        assertThat(response5.body().jsonPath().getString("data[0].description")).isEqualTo(newDescription);

    }

    @Test
    public void 없는회차정보수정() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();


        ExtractableResponse<Response> response = updateSessionRequest(accessToken, 432l, LocalDate.now(), "dsds");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    public void 호스트가아닌사람이회차정보수정() {
        String accessToken = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();


        ExtractableResponse<Response> response2 = createSessionRequest(accessToken, 1l, 1l, "김성하의c++세미나 1회차", LocalDate.now());
        ExtractableResponse<Response> response3 = createSessionRequest(accessToken, 1l, 2l, "김성하의c++세미나 1회차", LocalDate.now());
        ExtractableResponse<Response> response4 = getSessionsRequest(accessToken, 1l);

        Long sessionID = response4.body().jsonPath().getLong("data[0].id");
        String newDescription = "사실 정윤석이함";
        LocalDate date = LocalDate.of(2011, 11, 11);

        String accessToken2 = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = updateSessionRequest(accessToken2, sessionID, date, newDescription);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }

    @Test
    public void 수강신청() {
        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = applyActivityRequest(accessToken, 1l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().asString()).isEqualTo("수강신청에 성공하였습니다.");
    }

    @Test
    public void 아직열리지않은활동수강신청() {
        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = applyActivityRequest(accessToken, 3l);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("아직 신청할수 없습니다.");
    }

    @Test
    public void 수강신청인원초과() {
        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = applyActivityRequest(accessToken, 1l);

        String accessToken2 = loginRequest("MEMBER_ID3", "MEMBER_PASSWORD3")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response2 = applyActivityRequest(accessToken2, 1l);

        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.body().asString()).isEqualTo("정원을 초과하였습니다");
    }

    @Test
    public void 없는액티비티수강신청초과() {
        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = applyActivityRequest(accessToken, 432l);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void 수강신청한사람들조회() {
        String accessToken3 = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = applyActivityRequest(accessToken, 7l);

        String accessToken2 = loginRequest("MEMBER_ID3", "MEMBER_PASSWORD3")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response2 = applyActivityRequest(accessToken2, 7l);
        ExtractableResponse<Response> response3 = getActivityMembersRequest(accessToken, 7l);
        assertThat(response3.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response3.body().jsonPath().getList("data").size()).isEqualTo(2);

    }

    @Test
    public void 출석목록조회() {

        String accessToken3 = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = applyActivityRequest(accessToken3, 7l);

        String accessToken2 = loginRequest("MEMBER_ID3", "MEMBER_PASSWORD3")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response2 = applyActivityRequest(accessToken2, 7l);

        ExtractableResponse<Response> response3 = getActivityMembersRequest(accessToken, 7l);
        assertThat(response3.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response3.body().jsonPath().getList("data").size()).isEqualTo(2);

        ExtractableResponse<Response> response4 = createSessionRequest(accessToken3, 7l, 1l, "김성하의c++세미나 1회차", LocalDate.now());
        assertThat(response4.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> response5 = getAttendanceRequest(accessToken3, 10l);
        assertThat(response5.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response5.body().jsonPath().getList("data").size()).isEqualTo(2);

    }

    @Test
    public void 출석체크() {

        String accessToken3 = loginRequest("MEMBER_ID", "MEMBER_PASSWORD")
                .as(AuthResponse.class)
                .getAccessToken();

        String accessToken = loginRequest("MEMBER_ID2", "MEMBER_PASSWORD2")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response = applyActivityRequest(accessToken3, 7l);

        String accessToken2 = loginRequest("MEMBER_ID3", "MEMBER_PASSWORD3")
                .as(AuthResponse.class)
                .getAccessToken();

        ExtractableResponse<Response> response2 = applyActivityRequest(accessToken2, 7l);

        ExtractableResponse<Response> response3 = getActivityMembersRequest(accessToken, 7l);
        assertThat(response3.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response3.body().jsonPath().getList("data").size()).isEqualTo(2);

        ExtractableResponse<Response> response4 = createSessionRequest(accessToken3, 7l, 1l, "김성하의c++세미나 1회차", LocalDate.now());
        assertThat(response4.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> response5 = getAttendanceRequest(accessToken3, 10l);
        assertThat(response5.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response5.body().jsonPath().getList("data").size()).isEqualTo(2);

        List<Long> members = new ArrayList<>();
        members.add(response5.body().jsonPath().getLong("data[0].member.id"));
        members.add(response5.body().jsonPath().getLong("data[1].member.id"));
        ExtractableResponse<Response> response6 = attendRequest(accessToken3, 10l, members);

        ExtractableResponse<Response> response7 = getAttendanceRequest(accessToken3, 10l);

        assertThat(response7.body().jsonPath().getBoolean("data[0].attended")).isEqualTo(true);


    }

    public static ExtractableResponse<Response> getActivitiesRequest(String accessToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/activity")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getActivityRequest(String accessToken, Long id) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/activity/{activityID}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> createActivityRequest(String token, String title, String description, LocalDate startDate, Boolean isSeminar, String classHour, Long capacity, List<String> tags, Long hour) {
        ActivityCreateRequest request = new ActivityCreateRequest(title, description, startDate, isSeminar, classHour, capacity, hour, tags);
        return RestAssured
                .given().log().all()
                .auth().oauth2(token)
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/activity")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> updateActivityRequest(String token, Long id, String title, String description, LocalDate startDate, Boolean isSeminar, String classHour, Long capacity, List<String> tags, Long hour) {
        ActivityUpdateRequest request = new ActivityUpdateRequest(title, description, startDate, isSeminar, classHour, capacity, hour, tags);
        return RestAssured
                .given().log().all()
                .auth().oauth2(token)
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/activity/{activityID}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteActivityRequest(String token, Long id) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/activity/{activityID}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> createSessionRequest(String token, Long activityID, Long sessionID, String description, LocalDate date) {
        SessionCreateRequest request = new SessionCreateRequest(activityID, sessionID, date, description);
        return RestAssured
                .given().log().all()
                .auth().oauth2(token)
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/activity/session")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getSessionsRequest(String token, Long id) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/activity/session/{activityID}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> updateSessionRequest(String token, Long id, LocalDate date, String description) {
        SessionUpdateRequest request = new SessionUpdateRequest(date, description);
        return RestAssured
                .given().log().all()
                .auth().oauth2(token)
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/activity/session/{activityID}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> applyActivityRequest(String token, Long id) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/activity/apply/{activityID}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getActivityMembersRequest(String token, Long id) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/activity/member/{activityID}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getAttendanceRequest(String token, Long id) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/activity/check/{sessionID}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> attendRequest(String token, Long id, List<Long> members) {
        AttendanceRequest request = new AttendanceRequest(id, members);
        return RestAssured
                .given().log().all()
                .auth().oauth2(token)
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/activity/check")
                .then().log().all()
                .extract();
    }
}
