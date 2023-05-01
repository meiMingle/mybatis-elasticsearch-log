package cloud.pandas.plugin.mybatis.jvm;

import cloud.pandas.plugin.mybatis.constant.Constant;
import cloud.pandas.plugin.mybatis.executor.ExecutorUtil;
import cloud.pandas.plugin.mybatis.setting.MybatisLogSettingsState;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import java.io.File;
import java.util.UUID;

public class MybatisProgramPatcher extends JavaProgramPatcher {
    private static final Logger LOG = Logger.getInstance(MybatisProgramPatcher.class);

    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        if (ExecutorUtil.match(configuration)) {
            RunConfigurationBase<?> base = (RunConfigurationBase)configuration;
            PluginId pluginId = PluginId.getId("com.thtfpc.mybatis-log");
            IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(pluginId);
            if (plugin == null) {
                LOG.warn("load mybatis-log plugin failed...");
                return;
            }

            File file = plugin.getPluginPath().toFile();
            if (!file.exists()) {
                LOG.warn("load mybatis-log agent failed...");
                return;
            }

            ParametersList vmParametersList = javaParameters.getVMParametersList();
            vmParametersList.add("-Dapm.url=http://localhost:" + MybatisLogSettingsState.getInstance().port);
            vmParametersList.add("-Dmybatis.db_type=" + MybatisLogSettingsState.getInstance().dbType);
            String projectName = UUID.randomUUID().toString().split("-")[0];
            base.putUserData(Constant.PROJECT_NAME, projectName);
            vmParametersList.add("-Dapm.project=" + projectName);
            String agentPath = file.getAbsolutePath() + "/lib/skywalking-agent/skywalking-agent.jar";
            vmParametersList.add("-javaagent:" + agentPath);
        }
    }
}
