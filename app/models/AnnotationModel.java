package models;


import Utils.AnnotationUtils;
import com.mindtickle.annotation.AnnotationState;
import com.mindtickle.annotation.Comment;
import com.mindtickle.annotation.Social;
import dao.exception.DAOException;
import dao.IAnnotationDao;
import datastore.Couchbase;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class AnnotationModel  {


    public IAnnotationDao annotationDao;
    private static final Logger.ALogger logger = Logger.of(AnnotationModel.class);

    @Inject
    public AnnotationModel(IAnnotationDao annotationDao) {
        this.annotationDao = annotationDao;
    }


    public Social createSocialAnnotation(Social social) throws DAOException {
        Social newSocial = null;
        newSocial = (Social)annotationDao.createAnnotaion(social);
        return newSocial;
    }

    public Social createSocialAnnotationInsideParent(Social social) throws DAOException,NotFound {
        Social social1 = createSocialAnnotation(social);
        Social newSocial = null;
        newSocial = (Social)annotationDao.createAnnotaion(social);
        // put it inside parent
        List<String> socialIds = new ArrayList<String>();
        socialIds.add(AnnotationUtils.getSocialDBKey(newSocial.cname,newSocial.subjectId));
        Map<String,Social> socialMap = getSocialAnnotations(socialIds);
        if(socialMap.get(AnnotationUtils.getSocialDBKey(newSocial.cname,newSocial.subjectId)) != null) {
            Social parent = socialMap.get(AnnotationUtils.getSocialDBKey(newSocial.cname,newSocial.subjectId));
            parent.socialList.add(newSocial.socialId);
            annotationDao.updateAnnotation(parent);
        }
        else {
            throw new NotFound();
        }
        return newSocial;
    }

    public Map<String,Social> getSocialAnnotations(List<String> socialIds) throws  DAOException{
        Social social = null;
        Map<String,Social>  socialMap = annotationDao.getListOfAnnotation(socialIds);
        return socialMap;
    }

    public Social deleteSocialAnnotation(String cname,String annotationId) throws DAOException, NotFound {
        List<String> socialIds = new ArrayList<String>();
        String socialKey = AnnotationUtils.getSocialDBKey(cname,annotationId);
        socialIds.add(socialKey);
        Map<String,Social>  socialMap = getSocialAnnotations(socialIds);
        if(socialMap.get(socialKey) != null) {
            Social social = socialMap.get(socialKey);
            social.state = AnnotationState.DELETED;
            social = annotationDao.updateAnnotation(social);
            return social;
        }
        else {
            throw new NotFound();
        }
    }

    public Social updateComment(String cname,String annotationId,String text) throws DAOException,NotFound {
        List<String> socialIds = new ArrayList<String>();
        String socialKey = AnnotationUtils.getSocialDBKey(cname,annotationId);
        socialIds.add(socialKey);
        Map<String,Social>  socialMap = getSocialAnnotations(socialIds);
        if(socialMap.get(socialKey) != null) {
            // store edit version
            Comment  comment = (Comment)socialMap.get(socialKey);
            annotationDao.storeEditAnnotation(comment);
            // store the edited version
            comment.text = text;
            comment.version = comment.version + 1;
            comment = (Comment) annotationDao.updateAnnotation(comment);
            return comment;
        }
        else {
            throw new NotFound();
        }

    }


}
