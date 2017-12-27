package dao;

import Utils.AnnotationUtils;
import com.mindtickle.annotation.Comment;
import com.mindtickle.annotation.Social;
import com.mindtickle.annotation.SocialType;
import com.outjected.simpleflake.SimpleFlake;
import dao.exception.DAOException;
import datastore.Couchbase;
import play.Logger;

import play.libs.Json;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Singleton
public class AnnotationDao implements IAnnotationDao {
    public Couchbase couchbase;
    private static final Logger.ALogger logger = Logger.of(AnnotationDao.class);

    @Inject
    public AnnotationDao(Couchbase couchbase) {
        this.couchbase = couchbase;
    }


    @Override
    public Social createAnnotaion(Social social) throws DAOException  {
        try {
            social.socialId = Long.toString((new SimpleFlake()).generate());
            String key = social.cname + "|" + social.socialId;
            couchbase.set(key, Json.toJson(social).toString());
        } catch (InterruptedException e) {
            logger.error("error creating comment ",e);
            throw (new DAOException(e.getMessage()));
        } catch (ExecutionException e) {
            logger.error("error creating comment ",e);
            throw (new DAOException(e.getMessage()));
        }
        return social;
    }

    @Override
    public Social updateAnnotation(Social social) throws DAOException {
        try {
            String key = social.cname + "|" + social.socialId;
            couchbase.set(key, Json.toJson(social).toString());
        } catch (InterruptedException e) {
            logger.error("error creating comment ",e);
            throw (new DAOException(e.getMessage()));
        } catch (ExecutionException e) {
            logger.error("error creating comment ",e);
            throw (new DAOException(e.getMessage()));
        }
        return  social;
    }

    @Override
    public Social storeEditAnnotation(Social  social) throws DAOException {
        try {
            String key = AnnotationUtils.getSocialEditKey(social.cname,social.socialId,social.version);
            couchbase.set(key,Json.toJson(social).toString());

        }catch (InterruptedException e) {
            logger.error("error storing edit version of social ",e);
            throw (new DAOException(e.getMessage()));
        } catch (ExecutionException e) {
            logger.error("error storing edit version of social ",e);
            throw (new DAOException(e.getMessage()));
        }
        return social;
    }


    @Override
    public void getAnnotationById(String id) {

    }

    @Override
    public Map<String,Social>  getListOfAnnotation(List<String> annotationId) throws DAOException {
        Map<String,Social> stringSocialMap = new HashMap<String,Social>();
        try {
            Map<String, String> annotationResult = couchbase.getMulti(annotationId);
            for (String key : annotationId) {
                if (annotationResult.get(key) == null) {
                    logger.warn("Could not get social  value for key : " + key);
                    continue;
                }
                String json = annotationResult.get(key);
                Social social = AnnotationUtils.parseSocial(json);
                stringSocialMap.put(key,social);
            }
        } catch(IOException e) {
                logger.error("error parsing json response ");
                throw (new DAOException(e.getMessage()));
        } catch (Exception e) {
            logger.error("error getting multiple responses");
            throw (new DAOException(e.getMessage()));
        }
       return  stringSocialMap;
    }

    @Override
    public boolean deleteAnnotation(String id) {
        return false;
    }

    @Override
    public boolean editAnnotation() {
        return false;
    }
}
