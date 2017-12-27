package controllers;

import Utils.AnnotationUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindtickle.annotation.Comment;
import com.mindtickle.annotation.Social;
import datastore.Couchbase;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;


public class HomeControllerTest extends WithApplication {

    @Mock
    private Couchbase couchbaseMock;
    @Override
    protected Application provideApplication() {

        couchbaseMock = mock(Couchbase.class);
        return new GuiceApplicationBuilder()
                .overrides(bind(Couchbase.class).toInstance(couchbaseMock))
                        .build();
    }





    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }


    @Test
    public void testCreateSocialAnnotationCnameMustExist() throws IOException {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/createSocialAnnotation");
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"xyz\": \"xyz\",\"subjectId\":\"12345\",\"socialType\":\"COMMENT\" }");
        request.bodyJson(jsonNode);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void testCreateSocialAnnotationSubjectIdMustExist() throws IOException {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/createSocialAnnotation");
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"xyz\": \"xyz\",\"cname\":\"12345\",\"socialType\":\"COMMENT\" }");
        request.bodyJson(jsonNode);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void testCreateSocialAnnotationSocialTypeMustExist() throws IOException {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/createSocialAnnotation");
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"xyz\": \"xyz\",\"cname\":\"12345\",\"subjectId\":\"1341412\" }");
        request.bodyJson(jsonNode);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void testCreateSocialAnnotationTypeWithSocialType() throws IOException, ExecutionException, InterruptedException {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/createSocialAnnotation");
        Couchbase couchbaseMock = mock(Couchbase.class);
        when(couchbaseMock.set(any(String.class),any(String.class))).thenReturn(true);
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"socialType\": \"SOCIAL\",\"cname\":\"12345\",\"subjectId\":\"1341412\" }");
        request.bodyJson(jsonNode);
        Result result = route(app, request);
        assertEquals(200,result.status());
    }

    @Test
    public void testCreateSocialAnnotationWithCommentTypevideoTimeStampExist() throws IOException, ExecutionException, InterruptedException {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/createSocialAnnotation");
        Couchbase couchbaseMock = mock(Couchbase.class);
        when(couchbaseMock.set(any(String.class),any(String.class))).thenReturn(true);
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"socialType\": \"COMMENT\",\"cname\":\"12345\",\"subjectId\":\"1341412\",\"text\":\"12345\",\"author\":\"1341412\" }");
        request.bodyJson(jsonNode);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void testCreateSocialAnnotationWithCommentTypeAuthorExist() throws IOException, ExecutionException, InterruptedException {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/createSocialAnnotation");
        Couchbase couchbaseMock = mock(Couchbase.class);
        when(couchbaseMock.set(any(String.class),any(String.class))).thenReturn(true);
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"socialType\": \"COMMENT\",\"cname\":\"12345\",\"subjectId\":\"1341412\",\"text\":\"12345\",\"videoTimeStamp\":\"1341412\" }");
        request.bodyJson(jsonNode);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void testCreateSocialAnnotationWithCommentTypetextExist() throws IOException, ExecutionException, InterruptedException {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/createSocialAnnotation");
        Couchbase couchbaseMock = mock(Couchbase.class);
        when(couchbaseMock.set(any(String.class),any(String.class))).thenReturn(true);
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"socialType\": \"COMMENT\",\"cname\":\"12345\",\"subjectId\":\"1341412\",\"author\":\"12345\",\"videoTimeStamp\":\"1341412\" }");
        request.bodyJson(jsonNode);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void testCreateSocialAnnotationWithCommentType() throws IOException, ExecutionException, InterruptedException {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/createSocialAnnotation");
        Couchbase couchbaseMock = mock(Couchbase.class);
        when(couchbaseMock.set(any(String.class),any(String.class))).thenReturn(true);
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"socialType\": \"COMMENT\",\"cname\":\"12345\",\"subjectId\":\"1341412\",\"author\":\"12345\",\"videoTimeStamp\":\"1341412\",\"text\":\"fghijk\" }");
        request.bodyJson(jsonNode);
        Result result = route(app, request);
        assertEquals(200,result.status());
    }

    @Test
    public void testgetSocialAnnotationsWithAnnotationsArrayExist() throws IOException, ExecutionException, InterruptedException {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/getAnnotations");
        when(couchbaseMock.set(any(String.class),any(String.class))).thenReturn(true);
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"socialType\": \"COMMENT\"}");
        request.bodyJson(jsonNode);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void testgetSocialAnnotations() throws IOException, ExecutionException, InterruptedException, JSONException {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/getAnnotations");
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"socialType\": \"COMMENT\",\"cname\":\"12345\",\"subjectId\":\"1341412\",\"author\":\"12345\",\"videoTimeStamp\":\"1341412\",\"text\":\"fghijk\",\"socialId\":\"89765\" }");
        String couchbaseResponse = jsonNode.toString();
        Map<String,String> socialMap = new HashMap<String,String>();
        socialMap.put(AnnotationUtils.getSocialDBKey("12345","89765"),couchbaseResponse);
        List<String> socialAnnotationList = new ArrayList<String>();
        socialAnnotationList.add(AnnotationUtils.getSocialDBKey("12345","89765"));
        when(couchbaseMock.getMulti(any())).thenReturn(socialMap);
        JsonNode jsonRequest = (new ObjectMapper()).readTree("{ \"annotations\": [\"12345|89765\"]}");
        request.bodyJson(jsonRequest);
        Result result = route(app, request);
        String s = contentAsString(result);
        System.out.println(s);
        JSONObject jObject = new JSONObject(s);
        ObjectMapper mapper = new ObjectMapper();
        Iterator<?> keys = jObject.keys();
        Social social = null;
        while( keys.hasNext() ){
            String key = (String)keys.next();
            social = AnnotationUtils.parseSocial(jObject.getString(key));
        }
        assertEquals(200,result.status());
        assertEquals(social.socialId,"89765");
        assertEquals(social.subjectId,"1341412");
    }

    @Test
    public void deleteAnnotation() throws ExecutionException, InterruptedException, IOException {
        String cname = "12345";
        String socialId = "89765";
        String uri = "/" + cname + "/deleteAnnotation/" + socialId;
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .uri(uri);
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"socialType\": \"COMMENT\",\"cname\":\"12345\",\"subjectId\":\"1341412\",\"author\":\"12345\",\"videoTimeStamp\":\"1341412\",\"text\":\"fghijk\",\"socialId\":\"89765\",\"state\":\"ACTIVE\" }");
        String couchbaseResponse = jsonNode.toString();
        Map<String,String> socialMap = new HashMap<String,String>();
        socialMap.put(AnnotationUtils.getSocialDBKey("12345","89765"),couchbaseResponse);
        List<String> socialAnnotationList = new ArrayList<String>();
        socialAnnotationList.add(AnnotationUtils.getSocialDBKey("12345","89765"));
        when(couchbaseMock.getMulti(any())).thenReturn(socialMap);
        when(couchbaseMock.set(any(String.class),any(String.class))).thenReturn(true);
        Result result = route(app, request);
        String s = contentAsString(result);
        Social social = AnnotationUtils.parseSocial(s);
        assertEquals(200,result.status());
        assertEquals(social.state,"DELETED");
    }


    @Test
    public void testUpdateCommenttextExist() throws IOException, ExecutionException, InterruptedException {
        String cname = "12345";
        String socialId = "89765";
        String uri = "/" + cname + "/updateAnnotation/" + socialId;
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri(uri);
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"socialType\": \"COMMENT\"}");
        request.bodyJson(jsonNode);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST,result.status());
    }


    @Test
    public void testUpdateComment() throws IOException, ExecutionException, InterruptedException {
        String cname = "12345";
        String socialId = "89765";
        String uri = "/" + cname + "/updateAnnotation/" + socialId;
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri(uri);
        JsonNode jsonNode = (new ObjectMapper()).readTree("{ \"socialType\": \"COMMENT\",\"cname\":\"12345\",\"subjectId\":\"1341412\",\"author\":\"12345\",\"videoTimeStamp\":\"1341412\",\"text\":\"fghijk\",\"socialId\":\"89765\" }");
        String couchbaseResponse = jsonNode.toString();
        Map<String,String> socialMap = new HashMap<String,String>();
        socialMap.put(AnnotationUtils.getSocialDBKey("12345","89765"),couchbaseResponse);
        List<String> socialAnnotationList = new ArrayList<String>();
        socialAnnotationList.add(AnnotationUtils.getSocialDBKey("12345","89765"));
        when(couchbaseMock.getMulti(any())).thenReturn(socialMap);
        when(couchbaseMock.set(any(String.class),any(String.class))).thenReturn(true);
        JsonNode jsonRequest = (new ObjectMapper()).readTree("{ \"text\": \"mghij\"}");
        request.bodyJson(jsonRequest);
        Result result = route(app, request);
        String s = contentAsString(result);
        Comment comment = (Comment)AnnotationUtils.parseSocial(s);
        assertEquals(200,result.status());
        assertEquals(comment.text,"mghij");
    }







}
