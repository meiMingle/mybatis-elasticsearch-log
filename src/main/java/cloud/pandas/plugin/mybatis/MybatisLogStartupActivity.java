package cloud.pandas.plugin.mybatis;

import cloud.pandas.plugin.mybatis.constant.Constant;
import cloud.pandas.plugin.mybatis.constant.Holder;
import cloud.pandas.plugin.mybatis.executor.ExecutorUtil;
import cloud.pandas.plugin.mybatis.listener.ElasticsearchMessageListener;
import cloud.pandas.plugin.mybatis.listener.MybatisMessageListener;
import cloud.pandas.plugin.mybatis.service.MyBatisLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.TabbedPane;
import com.intellij.ui.TabbedPaneImpl;
import com.intellij.ui.content.Content;
import com.intellij.util.messages.Topic;
import java.util.Objects;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

public class MybatisLogStartupActivity implements StartupActivity {
    private static final Logger LOG = Logger.getInstance(MybatisLogStartupActivity.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void runActivity(@NotNull Project project) {
        MyBatisLogService webSocketService = (MyBatisLogService)ApplicationManager.getApplication().getService(MyBatisLogService.class);
        webSocketService.start();
        project.getMessageBus().connect().subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
            @Override
            public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
                if (ExecutorUtil.match(env.getRunProfile())) {
                    RunConfigurationBase<?> base = (RunConfigurationBase)env.getRunProfile();
                    String projectName = (String)base.getUserData(Constant.PROJECT_NAME);
                    Holder.init(projectName, env.getProject());
                    MybatisLogStartupActivity.this.attachConsoleTab(env, handler, projectName);
                }
            }
            @Override
            public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler, int exitCode) {
                if (ExecutorUtil.match(env.getRunProfile())) {
                    RunConfigurationBase<?> base = (RunConfigurationBase)env.getRunProfile();
                    String projectName = (String)base.getUserData(Constant.PROJECT_NAME);
                    Holder.destroy(projectName);
                }
            }
        });
    }

    private void attachConsoleTab(ExecutionEnvironment env, ProcessHandler handler, String projectName) {
        RunContentDescriptor runContentDescriptor = RunContentManager.getInstance(env.getProject()).findContentDescriptor(env.getExecutor(), handler);
        if (runContentDescriptor == null) {
            LOG.warn("runContentDescriptor is null");
        } else {
            RunnerLayoutUi runnerLayoutUi = runContentDescriptor.getRunnerLayoutUi();
            if (runnerLayoutUi == null) {
                LOG.warn("runnerLayoutUi is null");
            } else {
                TabbedPane pane = new TabbedPaneImpl(1);
                Content tab = runnerLayoutUi.createContent(Constant.APM_LOG, pane.getComponent(), Constant.APM_LOG, null, null);
                Project project = env.getProject();
                ConsoleViewImpl consoleView = new ConsoleViewImpl(project, true);
                Disposer.register(project, consoleView);
                pane.insertTab(Constant.MYBATIS_TAB_NAME, null, consoleView.getComponent(), null, 0);
                this.printToMybatisConsoleView(consoleView, projectName);
                consoleView = new ConsoleViewImpl(project, true);
                Disposer.register(project, consoleView);
                pane.insertTab(Constant.ELASTICSEARCH_TAB_NAME, null, consoleView.getComponent(), null, 0);
                this.printToElasticsearchConsoleView(consoleView, projectName);
                runnerLayoutUi.addContent(tab);
                LOG.info("loaded mybatis-log plugin");
            }
        }
    }

    private void printToMybatisConsoleView(ConsoleViewImpl consoleView, String projectName) {
        consoleView.getProject().getMessageBus().connect().subscribe((Topic)Holder.mybatisMap.get(projectName), (MybatisMessageListener)sqlWrapper -> {
            if (sqlWrapper != null) {
                synchronized(consoleView) {
                    String id = sqlWrapper.getId();
                    String sql = sqlWrapper.getSql();
                    int cost = sqlWrapper.getCost();
                    consoleView.print("===>" + id + "\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
                    consoleView.print(sql + "\n", ConsoleViewContentType.LOG_DEBUG_OUTPUT);
                    consoleView.print("<===" + id + ", cost time: " + cost + "ms\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
                }
            }
        });
    }

    private void printToElasticsearchConsoleView(ConsoleViewImpl consoleView, String projectName) {
        consoleView.getProject().getMessageBus().connect().subscribe((Topic)Holder.elasticsearchMap.get(projectName), (ElasticsearchMessageListener)dsl -> {
            if (dsl != null) {
                synchronized(consoleView) {
                    String method = dsl.getMethod();
                    String uri = dsl.getUri();
                    byte[] body = dsl.getBody();
                    consoleView.print(method + " " + uri + "\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
                    if (!Objects.isNull(body)) {
                        try {
                            if (uri.endsWith("/_bulk")) {
                                int lastIndex = 0;

                                while(true) {
                                    int index = ArrayUtils.indexOf(body, (byte)10, lastIndex);
                                    if (index < 0) {
                                        break;
                                    }

                                    byte[] subarray = ArrayUtils.subarray(body, lastIndex, index);
                                    String json = this.objectMapper.readTree(subarray).toString();
                                    consoleView.print(json + "\n", ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
                                    lastIndex = index + 1;
                                }
                            } else {
                                String json = this.objectMapper.readTree(body).toPrettyString();
                                consoleView.print(json + "\n", ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
                            }
                        } catch (Exception var12) {
                        }
                    }

                    int cost = dsl.getCost();
                    consoleView.print("cost time: " + cost + "ms\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
                }
            }
        });
    }
}
