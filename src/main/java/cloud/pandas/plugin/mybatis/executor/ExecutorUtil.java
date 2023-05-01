package cloud.pandas.plugin.mybatis.executor;

import com.google.common.collect.Sets;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfile;
import java.util.Set;

public class ExecutorUtil {
    private static final Set<String> CONFIG = Sets.newHashSet(
        new String[]{
            "com.intellij.execution.junit.JUnitConfiguration",
            "com.theoryinpractice.testng.configuration.TestNGConfiguration",
            "com.intellij.spring.boot.run.SpringBootApplicationRunConfiguration"
        }
    );

    public static boolean match(RunProfile configuration) {
        if (!(configuration instanceof RunConfigurationBase)) {
            return false;
        } else {
            String name = configuration.getClass().getName();
            return CONFIG.contains(name);
        }
    }
}
