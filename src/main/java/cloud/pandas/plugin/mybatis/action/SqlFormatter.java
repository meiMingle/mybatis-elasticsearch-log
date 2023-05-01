package cloud.pandas.plugin.mybatis.action;

import cloud.pandas.plugin.mybatis.constant.Constant;
import cloud.pandas.plugin.mybatis.setting.MybatisLogSettingsState;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class SqlFormatter {
    public static List<String> extraSql(List<String> lines) {
        List<MybatisSqlWrapper> mybatisSqlWrappers = Lists.newArrayList();
        int i = 0;

        for(int linesSize = lines.size(); i < linesSize; ++i) {
            if (!Objects.isNull(lines.get(i))) {
                int index1 = ((String)lines.get(i)).indexOf(Constant.PREPARING);
                if (index1 > 0) {
                    for(int j = i + 1; j < linesSize; ++j) {
                        int index2 = ((String)lines.get(j)).indexOf(Constant.TOTAL);
                        if (index2 > 0) {
                            String preparing = ((String)lines.get(i)).substring(index1 + Constant.PREPARING.length());
                            String join = Joiner.on(System.lineSeparator()).join(lines.subList(i, j));
                            int index3 = join.indexOf(Constant.PARAMETERS);
                            String parameters = join.substring(index3 + Constant.PARAMETERS.length());
                            mybatisSqlWrappers.add(new MybatisSqlWrapper(preparing, parameters));
                            i = j;
                            break;
                        }
                    }
                }
            }
        }

        return format(mybatisSqlWrappers);
    }

    private static List<String> format(List<MybatisSqlWrapper> mybatisSqlWrappers) {
        MybatisLogSettingsState state = MybatisLogSettingsState.getInstance();
        DbType dbType = DbType.valueOf(state.dbType);
        List<String> sqlEntities = Lists.newArrayList();

        for(MybatisSqlWrapper mybatisSqlWrapper : mybatisSqlWrappers) {
            String[] values = StringUtils.isEmpty(mybatisSqlWrapper.getParameters()) ? new String[0] : mybatisSqlWrapper.getParameters().split(", ");
            byte[] bytes = mybatisSqlWrapper.getPreparing().getBytes(StandardCharsets.UTF_8);
            int count = 0;

            for(byte b : bytes) {
                if (b == 63) {
                    ++count;
                }
            }

            if (count == 0 || count == values.length) {
                StringBuilder realSql = new StringBuilder();
                if (count == 0) {
                    for(byte b : bytes) {
                        realSql.append((char)b);
                    }
                } else {
                    count = 0;

                    for(byte b : bytes) {
                        if (b == 63) {
                            try {
                                String format = getValueFormat(values[count]);
                                realSql.append(format);
                                ++count;
                            } catch (Exception var15) {
                                realSql.append("ERRORS");
                            }
                        } else {
                            realSql.append((char)b);
                        }
                    }
                }

                String sql = realSql.toString();
                if (state.format) {
                    sql = SQLUtils.format(sql, dbType, SQLUtils.DEFAULT_FORMAT_OPTION);
                }

                sqlEntities.add(sql);
            }
        }

        return sqlEntities;
    }

    private static String getValueFormat(String valueWithType) {
        if (Constant.NULL.equals(valueWithType)) {
            return Constant.NULL;
        } else {
            int leftIndex = valueWithType.lastIndexOf("(");
            String realValue = valueWithType.substring(0, leftIndex);
            int rightIndex = valueWithType.lastIndexOf(")");
            String type = valueWithType.substring(leftIndex + 1, rightIndex);
            if (Boolean.class.getSimpleName().equals(type)) {
                return String.valueOf(BooleanUtils.toBoolean(type));
            } else if (Character.class.getSimpleName().equals(type)) {
                return " '" + realValue + "' ";
            } else if (Short.class.getSimpleName().equals(type)) {
                return realValue;
            } else if (Integer.class.getSimpleName().equals(type)) {
                return realValue;
            } else if (Long.class.getSimpleName().equals(type)) {
                return realValue;
            } else if (Float.class.getSimpleName().equals(type)) {
                return realValue;
            } else if (Double.class.getSimpleName().equals(type)) {
                return realValue;
            } else if (BigDecimal.class.getSimpleName().equals(type)) {
                return realValue;
            } else if (BigInteger.class.getSimpleName().equals(type)) {
                return realValue;
            } else if (Date.class.getSimpleName().equals(type)) {
                return "'" + realValue + "'";
            } else if (Time.class.getSimpleName().equals(type)) {
                return "'" + realValue + "'";
            } else if (Timestamp.class.getSimpleName().equals(type)) {
                return "'" + realValue + "'";
            } else {
                return String.class.getSimpleName().equals(type) ? "'" + realValue + "'" : "'" + realValue + "'";
            }
        }
    }
}
