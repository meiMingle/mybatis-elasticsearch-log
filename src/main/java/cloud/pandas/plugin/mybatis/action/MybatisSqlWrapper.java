package cloud.pandas.plugin.mybatis.action;

public class MybatisSqlWrapper {
    private String preparing;
    private String parameters;

    public MybatisSqlWrapper(String preparing, String parameters) {
        this.preparing = preparing;
        this.parameters = parameters;
    }

    public String getPreparing() {
        return this.preparing;
    }

    public void setPreparing(String preparing) {
        this.preparing = preparing;
    }

    public String getParameters() {
        return this.parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
