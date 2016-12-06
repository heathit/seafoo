package co.kr.myseafood.erp.net;

import co.kr.myseafood.erp.define.URL;

/**
 * Created by minu on 16. 5. 30..
 */
public class URLAdapter {

    /**
     * 재고조회
     * http://myseafood.co.kr/#/1/stock/index?item_id=12&warehouse_id=2&customer_id=1&size=1&origin=1&comment=2&wieght=1&date_from=2016-04-06&date_to=2016-05-31&date2_from=2016-04-25&date2_to=2016-05-30
     * 필수값 없음
     * @param sDate1
     * @param eDate1
     * @param sDate2
     * @param eDate2
     * @param custId
     * @param itemId
     * @param storeId
     * @param size
     * @param weight
     * @param origin
     * @param remark
     * @return
     */
    public static String getStockList(String sDate1, String eDate1, String sDate2, String eDate2, String custId, String itemId, String storeId, String size, String weight, String origin, String remark, String currentId){
//    public static String getStockList(StockListDO data){
        StringBuilder sb = new StringBuilder();

        if(sDate1 !=null && sDate1.length()>0){
            sb.append("&date_from=" + sDate1);
        }
        if(eDate1 !=null && eDate1.length()>0){
            sb.append("&date_to=" + eDate1);
        }
        if(sDate2 !=null && sDate2.length()>0){
            sb.append("&date2_from=" + sDate2);
        }
        if(eDate2 !=null && eDate2.length()>0){
            sb.append("&date2_to=" + eDate2);
        }
        if(custId !=null && custId.length()>0){
            sb.append("&customer_id=" + custId);
        }
        if(itemId !=null && itemId.length()>0){
            sb.append("&item_id=" + itemId);
        }
        if(storeId !=null && storeId.length()>0){
            sb.append("&warehouse_id=" + storeId);
        }
        if(size !=null && size.length()>0){
            sb.append("&size=" + size);
        }
        if(weight !=null && weight.length()>0){
            sb.append("&wieght=" + weight);
        }
        if(origin !=null && origin.length()>0){
            sb.append("&origin=" + origin);
        }
        if(remark !=null && remark.length()>0){
            sb.append("&comment=" + remark);
        }

        if(sb.length()>0){
            sb.delete(0, 1);
        }

        sb.insert(0, URL.SERVER + "/#/" + currentId + URL.STOCK_LIST);

        return sb.toString();
    }

    /**
     *  판매조회 및 수정 리스트 리플래쉬
     * 매출조회
     * http://myseafood.co.kr/#/1/sale/data/index?iid=11&date_from=2016-05-01&date_to=2016-05-30&comment=11&customer_id=2&item_id=2&warehouse_id=1
     * 필수값 : date_from / date_to
     * @param sDate
     * @param eDate
     * @param salesNo
     * @param remark
     * @param salesId
     * @param itemId
     * @param storeId
     * @return
     */
//    public static String getSalesList(SalesListDO data){
    public static String getSalesList(String sDate, String eDate, String salesNo, String remark, String salesId, String itemId, String storeId, String currentId){
        StringBuilder sb = new StringBuilder();
        sb.append(URL.SERVER + "/#/" + currentId + URL.SALE_LIST);

        if((sDate == null || sDate.length()==0)
            || (eDate == null || eDate.length()==0)){
            return null;
        }

        sb.append("date_from=" + sDate);
        sb.append("&date_to=" + eDate);

        if(salesNo !=null && salesNo.length()>0){
            sb.append("&iid=" + salesNo);
        }
        if(remark !=null && remark.length()>0){
            sb.append("&comment=" + remark);
        }
        if(salesId !=null && salesId.length()>0){
            sb.append("&customer_id=" + salesId);
        }
        if(itemId !=null && itemId.length()>0){
            sb.append("&item_id=" + itemId);
        }
        if(storeId !=null && storeId.length() > 0) {
            sb.append("&warehouse_id=" + storeId);
        }

        return sb.toString();
    }

    /**
     * 출고증 재발급
     * http://myseafood.co.kr/#/1/sale/release/index?date_from=2016-05-01&date_to=2016-05-30&customer_id=1&warehouse_id=2
     * 필수값 : date_from / date_to
     * @param sDate
     * @param eDate
     * @param custId
     * @param storeId
     * @return
     */
    public static String getFactoryCertList(String sDate, String eDate, String custId, String storeId, String currentId){
        StringBuilder sb = new StringBuilder();
        sb.append( URL.SERVER + "/#/" + currentId + URL.FACTORY_CERT2);

        if((sDate == null || sDate.length()==0)
                || (eDate == null || eDate.length()==0)){
            return null;
        }

        sb.append("date_from=" + sDate);
        sb.append("&date_to=" + eDate);

        if(custId !=null && custId.length()>0){
            sb.append("&customer_id=" + custId);
        }
        if(storeId !=null && storeId.length()>0){
            sb.append("&warehouse_id=" + storeId);
        }

        return sb.toString();
    }
}
