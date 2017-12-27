import com.google.inject.AbstractModule;
import dao.AnnotationDao;
import dao.IAnnotationDao;
import datastore.Couchbase;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
    //   Logger logger = ApplicationLogger.getLogger(Module.class);
      //  logger.info("aadamcnjwskamkqmajkdnqjkan");
        bind(Couchbase.class).asEagerSingleton();
        bind(IAnnotationDao.class).to(AnnotationDao.class);
    }
}
 // #  The implementation of the MDC with a ThreadLocal cannot work with this non-blocking asynchronous threading model.