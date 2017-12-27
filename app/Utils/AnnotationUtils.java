package Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindtickle.annotation.Comment;
import com.mindtickle.annotation.Social;
import com.mindtickle.annotation.SocialType;
import play.libs.Json;

import java.io.IOException;

public class AnnotationUtils {



    public static Social parseSocial(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        Social social = Json.fromJson(jsonNode, Social.class);
        if(SocialType.COMMENT == social.socialType) {
            social = Json.fromJson(jsonNode,Comment.class);
        }
        return social;
    }

    public static String getSocialDBKey(String cname,String socialId) {
        return cname + "|" + socialId;
    }

    public static String getSocialEditKey(String cname,String socialId,int version) {
        return cname + "|" + socialId + "|" + version;
    }

}
