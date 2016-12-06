package co.kr.myseafood.erp.net;

import java.io.Serializable;

/**
 * http 파라미터를 정의 한다
 *
 */
public class ReqParam implements Serializable {
    public String name = "";
    public String value = "";

    public ReqParam() {
    }

    public ReqParam(String _name, String _value) {
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
