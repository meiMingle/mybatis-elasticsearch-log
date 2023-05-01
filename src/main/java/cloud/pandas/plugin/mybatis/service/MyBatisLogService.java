package cloud.pandas.plugin.mybatis.service;

import cloud.pandas.plugin.mybatis.constant.Constant;
import cloud.pandas.plugin.mybatis.http.HttpServer;
import cloud.pandas.plugin.mybatis.setting.MybatisLogSettingsConfigurable;
import cloud.pandas.plugin.mybatis.setting.MybatisLogSettingsState;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import java.net.BindException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyBatisLogService implements Disposable {
    private final AtomicBoolean start = new AtomicBoolean(false);
    private final HttpServer httpServer;

    public MyBatisLogService() {
        int port = MybatisLogSettingsState.getInstance().port;
        this.httpServer = new HttpServer(port);
    }

    public void start() {
        try {
            if (this.start.compareAndSet(false, true)) {
                this.httpServer.start();
            }
        } catch (BindException var2) {
            this.start.compareAndSet(true, false);
            this.notifyError();
        }
    }

    private void notifyError() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        String context = String.format("Port(%d) is bound, please change another.", this.httpServer.getPort());
        Notification bindNotification = new Notification("", Constant.APM_LOG, context, NotificationType.WARNING);
        bindNotification.addAction(NotificationAction.create("Modify Port", (event, notification) -> {
            ShowSettingsUtil showSettingsUtil = ShowSettingsUtil.getInstance();
            showSettingsUtil.showSettingsDialog(event.getProject(), MybatisLogSettingsConfigurable.class);
            notification.hideBalloon();
        }));

        for(Project project : openProjects) {
            bindNotification.notify(project);
        }
    }

    public void dispose() {
        try {
            this.httpServer.stop();
        } catch (Exception var2) {
        }
    }

    public void restart(int port) {
        try {
            this.httpServer.restart(port);
        } catch (BindException var3) {
            this.notifyError();
        }
    }
}
