package co.kr.myseafood.erp.net;

/**
 * HTTP 헤더
 */
public class ReqHeader {
    public String name = "";
    public String value = "";

    public ReqHeader(String _name, String _value) {
        setName(_name);
        setValue(_value);
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public void setValue(String _value) {
        this.value = _value;
    }


}
