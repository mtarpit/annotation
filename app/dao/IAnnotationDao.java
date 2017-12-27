package dao;

import com.mindtickle.annotation.Comment;
import com.mindtickle.annotation.Social;
import com.mindtickle.annotation.SocialType;
import dao.exception.DAOException;

import java.util.List;
import java.util.Map;

public interface IAnnotationDao {

    public Social createAnnotaion(Social social) throws DAOException;
    public Social updateAnnotation(Social social) throws  DAOException;
    public Social storeEditAnnotation(Social social) throws DAOException;
    public void getAnnotationById(String id) throws DAOException;
    public Map<String,Social> getListOfAnnotation(List<String> annotationId) throws DAOException;
    public boolean deleteAnnotation(String id) throws DAOException;
    public boolean editAnnotation() throws DAOException;


}
