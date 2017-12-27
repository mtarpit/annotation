package datastore;



import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;
import play.Logger;
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.DefaultHashAlgorithm;
import net.spy.memcached.FailureMode;

import javax.inject.Inject;
import javax.inject.Singleton;
import play.inject.ApplicationLifecycle;

/**
 * The `Couchbase` class acts a simple connection manager for the
 * `CouchbaseClient` and makes sure that only one connection is alive throughout
 * the application.
 *
 * You may want to extend and harden this implementation in a production
 * environment.
 */
@Singleton
public  class Couchbase {

    private static final Logger.ALogger logger = Logger.of(Couchbase.class);

    private static Config config;
    private static final String HOSTS = "couchbase.hosts";
    private static CouchbaseClient geClient = null;
    private static CouchbaseClient blockingClient = null;
    public static final String UNICODE_END_CHAR = "\uefff";

    @Inject
    public Couchbase(ApplicationLifecycle appLifecycle,Config config) {
        this.config = config;
        logger.info("inside instance");
        connect();

            //  logger.info("in couchbase");
        appLifecycle.addStopHook(() -> {
            logger.info("Application stopped");
            disconnect();
            return CompletableFuture.completedFuture(null);
        });
    }



    public  boolean connect() {
        try{
            if(config.getString("couchbase.buckets.ge") == null){
                logger.error("Required properties not present : couchbase.buckets.ge");
            } else {
                logger.error("Aafasffadafcefed3edfefrerf32er4e3");
                geClient = createClient(config.getString("couchbase.buckets.ge"),config.getString("couchbase.buckets.ge_password"));
                logger.error("not able to find geclient");
            }
            if(config.getString("couchbase.buckets.annotation") == null){
                logger.error("Required properties not present : couchbase.buckets.blocking");
            } else {
                blockingClient = createClient(config.getString("couchbase.buckets.annotation"),config.getString("couchbase.buckets.annotation_password"));
            }
            logger.debug("cocuhbase annotation client created ");
        } catch (IOException e) {
            logger.error("Unable to find config in classpath.",e);
            System.exit(1);
        } catch (Exception e) {
            logger.error("Error creating couchbase client.",e);
            System.exit(1);
        }
        return true;
    }

    public  CouchbaseClient createClient(String bucket,String password) throws Exception {
        List<URI> hosts = new LinkedList<URI>();
        String hostsStr = config.getString(HOSTS);
        String[] hTokens = hostsStr.split(",");
        for(String hToken : hTokens) {
            try {
                logger.info("hosts");
                logger.error(hToken.trim());
                hosts.add(new URI(hToken.trim()));
            } catch (URISyntaxException e) {
                logger.error("Couchbase Error while trying to add host.", e);
                // ignore
            }
        }
        // TODO Other config. See if this needs to be configurable.
        CouchbaseConnectionFactory cf;
        CouchbaseConnectionFactoryBuilder cfb = new CouchbaseConnectionFactoryBuilder();
        cfb.setFailureMode(FailureMode.Redistribute);
        cfb.setMaxReconnectDelay(30);
        cfb.setReadBufferSize(16384);
        cfb.setOpTimeout(5000);
        cfb.setOpQueueMaxBlockTime(10000);
        cfb.setProtocol(CouchbaseConnectionFactoryBuilder.Protocol.BINARY);
        cfb.setLocatorType(CouchbaseConnectionFactoryBuilder.Locator.CONSISTENT);
        cfb.setHashAlg(DefaultHashAlgorithm.KETAMA_HASH);
        try {
            cf = cfb.buildCouchbaseConnection(hosts, bucket, password);
            CouchbaseClient client = new CouchbaseClient(cf);
            logger.info("Initialized couchbase client.");
            return client;
        } catch (Throwable t) {
            logger.error("Error in initializing couchbase client.", t);
            throw new Exception("Error in initializing couchbase client.");
        }
    }


    /**
     * Disconnect from Couchbase.
     */
    public  void disconnect() {
        if (geClient != null) {
            geClient.shutdown(3, TimeUnit.SECONDS);
        }
        if(blockingClient != null){
            blockingClient.shutdown(3, TimeUnit.SECONDS);
        }
    }

    public  CouchbaseClient getGEClient(){
        return geClient;
    }

    public  CouchbaseClient getBlockingClient(){
        return blockingClient;
    }

    public  boolean add(String key, String val) throws InterruptedException,ExecutionException {
        logger.debug("Adding Key "+key + " value " + val);
        //Context.current().args.put(key, val);
        boolean result = geClient.add(key, val).get();
        logger.debug("Done adding :" + result);
        return result;
    }

    public  boolean set(String key, String val) throws InterruptedException,ExecutionException {
        logger.debug("Setting Key "+key + " value " + val);
        //Context.current().args.put(key, val);
        boolean result = blockingClient.set(key, val).get();
        logger.debug("Done setting : " + result);
        return result;
    }

    public static boolean setWithCAS(String key, String val, long cas) throws InterruptedException,ExecutionException {
        logger.debug("Setting Key "+key + " value " + val + " cas " + cas);
        CASResponse resp = geClient.cas(key, cas, val);
        if(CASResponse.OK.equals(resp)){
            logger.debug("Done setting with CAS");
            return true;
        } else {
            logger.debug("Failed setting with CAS");
            return false;
        }
    }

    public  CASValue<Object> getWithCAS(String key){
        logger.debug("Getting Key with CAS: " + key);
        CASValue<Object> obj = geClient.gets(key);
        if (obj == null) {
            logger.debug("Getting Key value  with CAS : null ");
            return null;
        }
        logger.debug("Getting Key value  with CAS=" + obj.getCas() + " value="+obj.getValue().toString());
        return obj;
    }

    public  String get(String key) {
        logger.debug("Getting Key : " + key);
//		Object cacheObj = Context.current().args.get(key);
//		if(cacheObj != null)
//			return (String) cacheObj;
        Object obj = geClient.get(key);
        if (obj == null) {
            logger.debug("Getting Key value : null ");
            return null;
        }
        String result = (String) obj;
//		Context.current().args.put(key, result);
        logger.debug("Getting Key value : " + result);
        return result;
    }

    public  void delete(String key) {
        logger.debug("Deleting Key : " + key);
        //Context.current().args.remove(key);
        geClient.delete(key);
        logger.debug("Deleting Key Done : " + key);
    }

    public  Map<String, String> getMulti(Collection<String> keys) {
        Map<String, String> returnObjects = new HashMap<String, String>();
        logger.debug("Getting multi Keys : " + keys);
        if (keys == null || keys.size() == 0){
            logger.debug("No keys to fetch");
            return returnObjects;
        }
        Map<String, Object> objects = blockingClient.getBulk(keys);
        if (objects == null){
            logger.debug("Got null result from couchbase");
            return returnObjects;
        }

        for (Entry<String, Object> entry : objects.entrySet()) {
            returnObjects.put(entry.getKey(), (String) entry.getValue());
        }
        return returnObjects;
    }










}
