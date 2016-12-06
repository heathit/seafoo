package co.kr.myseafood.erp.data;

/**
 * Created by minu on 16. 5. 26..
 */
public class InterfaceDO {
    private String id;
    private String name;
    private String aliased_workplace_id;

    public InterfaceDO(
            String id
            , String name) {
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

    public String getAliased_workplace_id() {
        return aliased_workplace_id;
    }

    public void setAliased_workplace_id(String aliased_workplace_id) {
        this.aliased_workplace_id = aliased_workplace_id;
    }
}
