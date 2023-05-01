package cloud.pandas.plugin.mybatis.setting;

import com.alibaba.druid.DbType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "cloud.pandas.plugin.mybatis.setting.MybatisLogSettingsState",
    storages = {@Storage("MybatisLogSettingsPlugin.xml")}
)
public class MybatisLogSettingsState implements PersistentStateComponent<MybatisLogSettingsState> {
    public boolean format = true;
    public String dbType = DbType.mysql.name();
    public int port = 5866;

    public static MybatisLogSettingsState getInstance() {
        return (MybatisLogSettingsState)ApplicationManager.getApplication().getService(MybatisLogSettingsState.class);
    }

    @Nullable
    public MybatisLogSettingsState getState() {
        return this;
    }

    public void loadState(@NotNull MybatisLogSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
