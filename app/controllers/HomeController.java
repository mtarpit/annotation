package controllers;

import Utils.AnnotationUtils;
import actions.InitAction;
import com.mindtickle.annotation.Social;
import com.mindtickle.annotation.SocialType;
import dao.exception.DAOException;
import models.AnnotationModel;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import play.Logger;
import play.libs.Json;
import play.mvc.*;
import com.fasterxml.jackson.databind.JsonNode;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@With(InitAction.class)
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    @Inject
    AnnotationModel annotationModel;

    private static final Logger.ALogger logger = Logger.of(HomeController.class);

    public Result index() {
        return ok(views.html.index.render());
    }

// this annotation automatically check for non json response and throw the error
    @BodyParser.Of(BodyParser.Json.class)
    public Result createSocialAnnotation() {
        Social newSocial = null;
        try {
            JsonNode jsonNode = request().body().asJson();
            if(!jsonNode.hasNonNull("cname") || !jsonNode.hasNonNull("subjectId") || !jsonNode.hasNonNull("socialType")){
                return badRequest("subjectId cname must be present in the json");
            }
            Social social = AnnotationUtils.parseSocial(jsonNode.toString());
            if(social.socialType == SocialType.COMMENT && (!jsonNode.hasNonNull("videoTimeStamp")  || !jsonNode.hasNonNull("text") || !jsonNode.hasNonNull("author"))){
                return badRequest("text  author subjectId cname must be present");
            }
            newSocial = annotationModel.createSocialAnnotation(social);
        } catch (DAOException e) {
            logger.error("exception occured while creating comment ",e);
            return internalServerError("error creating comment annotation");
        } catch (IOException e) {
            logger.error("parsing json response",e);
            return internalServerError("error parsing json request for creating social");
        }
        return ok(Json.toJson(newSocial));

    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result createSocialAnnotationInsideParent() {
        Social newSocial = null;
        try {
            JsonNode jsonNode = request().body().asJson();
            if(!jsonNode.hasNonNull("cname") || !jsonNode.hasNonNull("subjectId") || !jsonNode.hasNonNull("socialType")){
                return badRequest("subjectId cname must be present in the json");
            }
            Social social = AnnotationUtils.parseSocial(jsonNode.toString());
            if(social.socialType == SocialType.COMMENT && (!jsonNode.hasNonNull("videoTimeStamp")  || !jsonNode.hasNonNull("text") || !jsonNode.hasNonNull("author"))){
                return badRequest("text  author subjectId cname must be present");
            }
            newSocial = annotationModel.createSocialAnnotation(social);
        } catch (DAOException e) {
            logger.error("exception occured while creating comment ",e);
            return internalServerError("error creating comment annotation");
        } catch (IOException e) {
            logger.error("parsing json response",e);
            return internalServerError("error parsing json request for creating social");
        }
        return ok(Json.toJson(newSocial));

    }



    @BodyParser.Of(BodyParser.Json.class)
    public Result getListOfSocialAnnotation() {
        Map<String,Social> annotations = null;
        try  {
                JsonNode jsonNode = request().body().asJson();
                if(!jsonNode.hasNonNull("annotations")) {
                    return badRequest("annotations array not found");
                }
                List<String> socialIds = (List<String>)Json.fromJson(jsonNode.get("annotations"),List.class);
                annotations = annotationModel.getSocialAnnotations(socialIds);
        }catch (Exception e) {
                logger.error("error fetching list of annotations",e);
                return internalServerError("error creating comment annotation");
        }
        return ok(Json.toJson(annotations));
    }

    public Result deleteAnnotation(String cname,String id) {
        Social social = null;
        try  {
                social =  annotationModel.deleteSocialAnnotation(cname,id);
            } catch (DAOException e) {
                return  internalServerError("deleting of annotation causing error");
            } catch (NotFound e) {
                logger.error("annotation id not exist in database",e);
                return notFound();
            }
            return ok(Json.toJson(social));
    }

    public Result updateComment(String cname,String id) {
        Social social = null;
        try  {
            JsonNode jsonNode = request().body().asJson();
            if(!jsonNode.hasNonNull("text")) {
                return badRequest("text field in json not found");
            }
            String text = jsonNode.get("text").asText();
            social =  annotationModel.updateComment(cname,id,text);
        } catch (DAOException e) {
            return  internalServerError("deleting of annotation causing error");
        } catch (NotFound e) {
            logger.error("annotation id not exist in database",e);
            return notFound();
        }
        return ok(Json.toJson(social));
    }
}
