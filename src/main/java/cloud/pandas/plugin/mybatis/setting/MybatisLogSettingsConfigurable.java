package cloud.pandas.plugin.mybatis.setting;

import cloud.pandas.plugin.mybatis.constant.Constant;
import cloud.pandas.plugin.mybatis.service.MyBatisLogService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import java.util.Objects;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nullable;

public class MybatisLogSettingsConfigurable implements Configurable {
    private MybatisLogSettingsDialog setting;

    public String getDisplayName() {
        return Constant.APM_LOG;
    }

    public JComponent getPreferredFocusedComponent() {
        return this.setting.getPreferredFocusedComponent();
    }

    @Nullable
    public JComponent createComponent() {
        this.setting = new MybatisLogSettingsDialog();
        return this.setting.getPanel();
    }

    public boolean isModified() {
        MybatisLogSettingsState settings = MybatisLogSettingsState.getInstance();
        boolean modified = this.setting.getFormat() != settings.format;
        modified |= !Objects.equals(this.setting.getDbType(), settings.dbType);
        return modified | !Objects.equals(this.setting.getPort(), settings.port);
    }

    public void apply() {
        MybatisLogSettingsState settings = MybatisLogSettingsState.getInstance();
        settings.format = this.setting.getFormat();
        settings.dbType = this.setting.getDbType();
        if (!Objects.equals(settings.port, this.setting.getPort())) {
            MyBatisLogService logService = (MyBatisLogService)ApplicationManager.getApplication().getService(MyBatisLogService.class);
            logService.restart(this.setting.getPort());
        }

        settings.port = this.setting.getPort();
    }

    public void reset() {
        MybatisLogSettingsState settings = MybatisLogSettingsState.getInstance();
        this.setting.setFormat(settings.format);
        this.setting.setDbType(settings.dbType);
        this.setting.setPort(settings.port);
    }

    public void disposeUIResources() {
        this.setting = null;
    }
}
