package co.kr.myseafood.erp.data;

/**
 * Created by minu on 2016-05-15.
 */
public class WorkplaceDO {
    private String id;
    private String name;

    public WorkplaceDO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
