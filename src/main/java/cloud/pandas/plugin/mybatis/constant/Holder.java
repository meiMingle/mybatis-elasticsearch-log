package cloud.pandas.plugin.mybatis.constant;

import cloud.pandas.plugin.mybatis.listener.ElasticsearchMessageListener;
import cloud.pandas.plugin.mybatis.listener.MybatisMessageListener;
import com.google.common.collect.Maps;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import java.util.Map;

public class Holder {
    public static Map<String, Project> projectMap = Maps.newConcurrentMap();
    public static Map<String, Topic<MybatisMessageListener>> mybatisMap = Maps.newConcurrentMap();
    public static Map<String, Topic<ElasticsearchMessageListener>> elasticsearchMap = Maps.newConcurrentMap();

    public static void init(String projectName, Project project) {
        projectMap.put(projectName, project);
        mybatisMap.putIfAbsent(projectName, Topic.create(projectName, MybatisMessageListener.class));
        elasticsearchMap.putIfAbsent(projectName, Topic.create(projectName, ElasticsearchMessageListener.class));
    }

    public static void destroy(String projectName) {
        try {
            projectMap.remove(projectName);
            mybatisMap.remove(Constant.MYBATIS_TAB_NAME + ":" + projectName);
            elasticsearchMap.remove(Constant.ELASTICSEARCH_TAB_NAME + ":" + projectName);
        } catch (Exception var2) {
        }
    }
}
