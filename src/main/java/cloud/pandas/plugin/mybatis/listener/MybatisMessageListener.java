package cloud.pandas.plugin.mybatis.listener;

import cloud.pandas.plugin.mybatis.entity.SqlWrapper;

public interface MybatisMessageListener {
    void changed(SqlWrapper var1);
}
