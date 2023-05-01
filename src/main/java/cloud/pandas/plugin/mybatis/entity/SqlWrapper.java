package cloud.pandas.plugin.mybatis.entity;

public class SqlWrapper {
    private String id;
    private String sql;
    private Integer cost;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Integer getCost() {
        return this.cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public String toString() {
        return "SqlWrapper{id='" + this.id + "', sql='" + this.sql + "', cost=" + this.cost + "}";
    }
}
