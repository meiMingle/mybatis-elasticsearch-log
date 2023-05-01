package cloud.pandas.plugin.mybatis.constant;

import com.intellij.openapi.util.Key;

public class Constant {
    public static String APM_LOG = "APM Log";
    public static String MYBATIS_TAB_NAME = "Mybatis Log";
    public static String ELASTICSEARCH_TAB_NAME = "Elasticsearch Request";
    public static String PREPARING = "==>  Preparing:";
    public static String PARAMETERS = "==> Parameters:";
    public static String TOTAL = "<==      Total:";
    public static String NULL = "null";
    public static Key<String> PROJECT_NAME = Key.create("PROJECT_NAME");
}
