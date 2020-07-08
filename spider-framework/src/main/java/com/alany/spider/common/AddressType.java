package com.alany.spider.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alany on 2020/7/2.
 */
public enum AddressType {
    province("province"),
    city("city"),
    county("county"),
    town("town"),
    village("village");

    private String type;

    AddressType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    static String regex = "(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<village>.*)";

    public static String regexAddress(String address, AddressType addressType) {
        String provinceRegex = "(?<province>[^省]+自治区|.*?省|.*?行政区)";
        Matcher m = Pattern.compile(provinceRegex).matcher(address);
        address = m.find() ? address : "省" + address; //没有省的情况需要加上
        m = Pattern.compile(regex).matcher(address);
        String province = null, city = null, county = null, town = null, village = null;
        Map<AddressType, String> matchMap = new LinkedHashMap<AddressType, String>();
        while (m.find()) {
            province = m.group("province");
            matchMap.put(AddressType.province, province == null ? "" : province.trim());
            city = m.group("city");
            matchMap.put(AddressType.city, city == null ? "" : city.trim());
            county = m.group("county");
            matchMap.put(AddressType.county, county == null ? "" : county.trim());
            town = m.group("town");
            matchMap.put(AddressType.town, town == null ? "" : town.trim());
            village = m.group("village");
            matchMap.put(AddressType.village, village == null ? "" : village.trim());
        }
        return matchMap.get(addressType);
    }
}
