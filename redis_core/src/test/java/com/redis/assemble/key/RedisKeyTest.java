package com.redis.assemble.key;

import com.redis.common.exception.ReadConfigException;
import com.redis.config.PoolManagement;
import com.redis.config.PropertyFile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by https://github.com/kuangcp on 17-6-20  下午8:02
 */
public class RedisKeyTest {
//    @Mock //用@Mocked标注的对象，不需要赋值，jmockit自动mock
//    Logger logger;
//    @Mock
//    PoolManagement management;
//    @Mock
//    RedisPools pools;
//    @Mock
//    Jedis jedis;
//    @InjectMocks
    RedisKey redisKey;
    String key="testKey";

    @Before
    public void setUp() throws ReadConfigException {
        // TODO 理解JMockit 原理，这里写这个和自己写的测试类都没差了，测试花费的时间是个问题
        MockitoAnnotations.initMocks(this);
        PoolManagement management = PoolManagement.getInstance();
        management.setCurrentPoolId(PropertyFile.getMaxId()+"");
        redisKey = RedisKey.getInstance();
    }
    @Test
    public void createData1(){
        for(int i=0;i<5000;i++){
            redisKey.set("aaa"+i,"dfdfdfdf");
        }
    }
    @Test
    public void createData2(){
        for(int i=0;i<5000;i++){
            redisKey.set("qq"+i,"dfdfdfdf");
        }
    }
    @Test
    public void createData3(){
        for(int i=0;i<5000;i++){
            redisKey.set("pp"+i,"dfdfdfdf");
        }
    }
    @Test
    public void testStatus(){
        Map<String,String> map = redisKey.getStatus();
//        for(String key:map.keySet()){
//            System.out.println(key+"_______"+map.get(key));
//        }
        System.out.println(map.get("db0"));
    }

    // 测试了set get delete
    @Test
    public void testGetSet() throws Exception {

        redisKey.set(key,"gg");
        String result = redisKey.get(key);
        testDeleteKey();
        Assert.assertEquals("gg", result);

    }
    public void testDeleteKey() throws Exception {
        long result = redisKey.deleteKey(key);
        Assert.assertEquals(1L, result);
    }

    @Test
    public void testDump() throws Exception {
        byte[] result = redisKey.dump(key);
        Assert.assertArrayEquals(null, result);
    }

    @Test
    public void testSetExpire() throws Exception {
        Long result = redisKey.setExpire(key, 0, "value");
        assert(1L == result);
    }

    @Test
    public void testSetExpireMs() throws Exception {
        String result = redisKey.setExpireMs(key, 12L, "value");
        Assert.assertEquals("OK", result);
    }

    @Test
    public void testIncreaseKey() throws Exception {
        redisKey.set(key,"1");
        long result = redisKey.increaseKey(key);
        Assert.assertEquals(2L, result);
        testDeleteKey();
    }

    @Test
    public void testDecreaseKey() throws Exception {
        redisKey.set(key,"1");
        long result = redisKey.decreaseKey(key);
        Assert.assertEquals(0L, result);
        testDeleteKey();
    }

    @Test
    public void testAppend() throws Exception {
        redisKey.set(key,"1");
        long result = redisKey.append(key, "value");
        Assert.assertEquals(6L, result);
    }

    @Test
    public void testGetMultiKeys() throws Exception {
        redisKey.set(key,"1");
        redisKey.set("1","2");
        List<String> result = redisKey.getMultiKeys(key,"1");
        Assert.assertEquals(Arrays.<String>asList("1","2"), result);
        testDeleteKey();
        redisKey.deleteKey("1");
    }

    @Test
    public void testSetMultiKey() throws Exception {
        String result = redisKey.setMultiKey("keys","a",key,"b");
        Assert.assertEquals("OK", result);
        List<String> results = redisKey.getMultiKeys(key,"keys");
        Assert.assertEquals(Arrays.<String>asList("b","a"), results);
        testDeleteKey();
        redisKey.deleteKey("keys");
    }

    @Test
    public void testGetEncoding() throws Exception {
        redisKey.set(key,"78.89");
        String result = redisKey.getEncoding(key);
        Assert.assertEquals("embstr", result);
        testDeleteKey();
    }
    @Test
    public void testListKey(){
        Set<String> sets = redisKey.listAllKeys(redisKey.getDb());
        for(String d:sets){
            System.out.println(d);
        }
        System.out.println(sets.size());
    }

//     TODO ???? 差这个方法没有测试,测试没有通过
//    @Test
//    public void testListKeys() throws Exception {
//        Set<Elements> result = redisKey.listKeys();
//        Assert.assertEquals(new HashSet<Elements>(Arrays.asList(new Elements(0, "id", key, ElementsType.ROOT, Order.Ascend))), result);
//    }

    @Test
    public void testType() throws Exception {
        redisKey.set(key,"12");
        String result = redisKey.type(key);
        Assert.assertEquals("string", result);
    }

    @Test
    public void testExpire() throws Exception {
        long result = redisKey.expire(key, 0);
        Assert.assertEquals(1L, result);
    }

//    @Test
//    public void testFlushAll() throws Exception {
//        String result = redisKey.flushAll();
//        Assert.assertEquals("OK", result);
//    }
//
//    @Test
//    public void testFlushDB() throws Exception {
//        String result = redisKey.flushDB(0);
//        Assert.assertEquals("OK", result);
//    }
}
