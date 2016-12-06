package co.kr.myseafood.erp.define;

import co.kr.myseafood.erp.util.Util;

/**
 * Created by minu on 16. 5. 18..
 */
public class URL {

    public static final String PROTOCOL = "http";
    public static final String PORT     = "8080";
    public static final String SERVER	= PROTOCOL + "://myseafood.co.kr:" + PORT;
    public static final String WEBURL  	= PROTOCOL + "://myseafood.co.kr";

    /*
     * 서버 API
     */
    // 로그인
    public static final String LOGIN = "/api/v1/auth/login";
    // 회원가입
    public static final String JOIN = "/api/v1/registeruser";
    // status
    public static final String STATUS = "/api/v1/auth/status";
    // 메뉴
    public static final String ACC_MAIN_CATEGORY = "/api/v1/account_main_category";
    // 알림 갯수 _GET
    public static final String ALERT_CNT = "/api/v1/purchase_queues_count";


    // 품목
    public static final String ITEM_ARRAY = "/api/v1/item";
    // 창고
    public static final String STORE_ARRAY = "/api/v1/warehouse";
    // 매출처
    public static final String SALES_ARRAY = "/api/v1/customer";
    // 거래처
    public static final String CUST_ARRAY = "/api/v1/customer";
    // 매입처
    public static final String PURCHASE_ARRAY = "/api/v1/customer";
    // 거래은행
    public static final String BANK_ARRAY = "/api/v1/bank";

    /*
     * web화면
     */
    // 매입후판매
//    public static final String SALE_PURCHASE = SERVER + "/#/1/purchase/data/pipe";
    public static final String SALE_PURCHASE = "/purchase/data/pipe";
    // 판매조회.수정
    public static final String SALE_LIST = "/sale/data/index?";
    public static String get_sale_list(){
        String date = Util.getDate("yyyy-MM-dd");

        StringBuilder sb = new StringBuilder();
        sb.append(SALE_LIST);
        sb.append("date_from=" + date.substring(0, date.length()-2) + "01");
        sb.append("&");
        sb.append("date_to=" + date);

        return sb.toString();
    }

    // 재고조회
    public static final String STOCK_LIST = "/stock/index?";
    // 재고판매
    public static final String SALE_STOCK = "/sale/data/store";
    // 출고증
    public static final String FACTORY_CERT = "/sale/release/store";
    // 출고증 재발급
    public static final String FACTORY_CERT2 = "/sale/release/index?";
    public static String get_factory_cert2(){
        String date = Util.getDate("yyyy-MM-dd");

        StringBuilder sb = new StringBuilder();
        sb.append(FACTORY_CERT2);
        sb.append("date_from=" + date.substring(0, date.length()-2) + "01");
        sb.append("&");
        sb.append("date_to=" + date);

        return sb.toString();
    }
    // 거래처조회
    public static final String CUST_LIST = "/code/customer/index";
    // 공유자료
    public static final String FILE_MANAGER = "/filemanager";
    // 알림
    public static final String ALERT = "/purchase/data/queue";
    // 모바일 팩스
    public static final String MOBILE_FAX = "/fax/sent/store";
    public static final String MOBILE_FAX_SENTLIST = "/fax/sent/index";

}
